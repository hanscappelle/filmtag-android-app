package be.hcpl.android.filmtag.ui.tranformer

import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.ui.view.FrameUiModel

class FrameUiModelTransformer(
    private val textTransformer: TextTransformer,
) {

    fun transform(frame: Frame) = FrameUiModel(
        number = frame.number,
        frameNumber = textTransformer.formatFrameNumber(frame.number),
        dateTaken = frame.dateTaken?.let { textTransformer.formatDate(it) } ?: "-",
        apertureAndShutter = textTransformer.formatApertureAndShutter(
            frame.aperture,
            frame.shutter,
            frame.isLongExposure
        ),
        frameNotes = frame.notes ?: "-",
    )
}