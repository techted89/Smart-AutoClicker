package com.buzbuz.smartautoclicker.core.bitmaps

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class BitmapLRUCacheTest {

    private val bitmapLRUCache = BitmapLRUCache()

    @Test
    fun getImageConditionBitmapOrDefault_whenCached_returnsBitmap() = runTest {
        val path = "test_path"
        val width = 100
        val height = 100
        val bitmap = mock<Bitmap>()
        whenever(bitmap.byteCount).thenReturn(1024)

        // Pre-populate cache
        bitmapLRUCache.putImageConditionBitmap(path, width, height, bitmap)

        // Call suspend function
        val result = bitmapLRUCache.getImageConditionBitmapOrDefault(path, width, height) {
            null // Should not be called
        }

        assertEquals(bitmap, result)
    }

    @Test
    fun getImageConditionBitmapOrDefault_whenNotCached_callsInsertAndCaches() = runTest {
        val path = "test_path_miss"
        val width = 100
        val height = 100
        val bitmap = mock<Bitmap>()
        whenever(bitmap.byteCount).thenReturn(1024)

        var insertCalled = false
        val insert: suspend () -> Bitmap? = {
            insertCalled = true
            bitmap
        }

        // Call suspend function
        val result = bitmapLRUCache.getImageConditionBitmapOrDefault(path, width, height, insert)

        assertEquals(bitmap, result)
        assertTrue(insertCalled)

        // Verify it's cached
        val cached = bitmapLRUCache.getImageConditionBitmapOrDefault(path, width, height) {
            null
        }
        assertEquals(bitmap, cached)
    }

    @Test
    fun getImageConditionBitmapOrDefault_whenInsertReturnsNull_returnsNull() = runTest {
        val path = "test_path_null"
        val width = 100
        val height = 100

        val result = bitmapLRUCache.getImageConditionBitmapOrDefault(path, width, height) {
            null
        }

        assertNull(result)
    }
}
