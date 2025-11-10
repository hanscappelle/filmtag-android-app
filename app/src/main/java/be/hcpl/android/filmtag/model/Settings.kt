package be.hcpl.android.filmtag.model

data class Settings(
    val frameCount: Int,
    val iso: Int,
    val shutter: Int,
    val aperture: Float,
    val limitNotesPreview: Int,
)