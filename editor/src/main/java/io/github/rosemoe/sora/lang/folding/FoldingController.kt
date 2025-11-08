/*******************************************************************************
 *    sora-editor - the awesome code editor for Android
 *    https://github.com/Rosemoe/sora-editor
 *    Copyright (C) 2020-2025  Rosemoe
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 *
 *     Please contact Rosemoe by email 2073412493@qq.com if you need
 *     additional information or have any questions
 ******************************************************************************/

package io.github.rosemoe.sora.lang.folding

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 *
 * Class that glues the code folding functionalities between `FoldingModel`, `FoldingProvider` and the `CodeEditor`
 *
 * @author itSMcodez
 */
class FoldingController(
    private val model: FoldingModel,
    private val provider: FoldingProvider
) {

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    @RequiresApi(Build.VERSION_CODES.N)
    fun refresh(documentUri: String, onComplete: (() -> Unit)? = null) {
        executor.submit {
            provider.requestFoldingRegions(documentUri)
                .thenAccept { regions ->
                    model.setRegions(regions)
                    onComplete?.invoke()
                }
        }
    }

    fun getModel(): FoldingModel = model
}
