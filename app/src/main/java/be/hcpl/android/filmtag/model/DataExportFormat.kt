package be.hcpl.android.filmtag.model

import androidx.annotation.Keep

/**
 * Needed only to combine frame and roll data in a single format that can be exported

 * Created by hcpl on 2/08/15.
 */
@Keep
class DataExportFormat {

    var rolls: List<Roll>? = null

    var frames: Map<Long, List<Frame>>? = null
}
