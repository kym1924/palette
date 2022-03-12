package com.kimym.palette

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.palette.graphics.Palette
import com.kimym.palette.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        createPaletteAsync()
    }

    private fun createPaletteAsync() {
        Palette.from(binding.image.drawable.toBitmap()).generate { palette ->
            palette?.let {
                initSwatch(BR.lightVibrant, it.lightVibrantSwatch)
                initSwatch(BR.vibrant, it.vibrantSwatch)
                initSwatch(BR.darkVibrant, it.darkVibrantSwatch)
                initSwatch(BR.lightMuted, it.lightMutedSwatch)
                initSwatch(BR.muted, it.mutedSwatch)
                initSwatch(BR.darkMuted, it.darkMutedSwatch)
            }
        }
    }

    private fun initSwatch(dataBindingId: Int, swatch: Palette.Swatch?) {
        swatch?.let {
            Timber.d(swatch.toString())
            binding.setVariable(dataBindingId, swatch)
        } ?: Timber.d(getString(R.string.not_generated))
    }
}
