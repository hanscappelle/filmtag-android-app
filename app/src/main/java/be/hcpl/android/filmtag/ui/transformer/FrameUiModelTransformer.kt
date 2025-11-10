package be.hcpl.android.filmtag.ui.transformer

import be.hcpl.android.filmtag.domain.SettingsRepository
import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.ui.view.FrameUiModel

class FrameUiModelTransformer(
    private val textTransformer: TextTransformer,
    private val settingsRepository: SettingsRepository,
) {

    fun transform(frame: Frame): FrameUiModel {
        val formattedNotes = frame.notes ?: ""
        val previewLimit = settingsRepository.retrieveSettings().limitNotesPreview.takeIf { it > 0 && it < formattedNotes.length }
        return FrameUiModel(
            number = frame.number,
            frameNumber = textTransformer.formatFrameNumber(frame.number),
            dateTaken = frame.dateTaken?.let { textTransformer.formatDate(it) } ?: "-",
            apertureAndShutter = textTransformer.formatApertureAndShutter(
                frame.aperture,
                frame.shutter,
                frame.isLongExposure
            ),
            frameNotes = previewLimit?.let { "${formattedNotes.substring(0, previewLimit)}..." } ?: formattedNotes,
        )
    }
}