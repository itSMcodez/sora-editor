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

package io.github.rosemoe.sora.lsp.editor

import android.util.Log
import io.github.rosemoe.sora.lang.folding.FoldingProvider
import io.github.rosemoe.sora.lang.folding.FoldingRegion
import org.eclipse.lsp4j.FoldingRangeRequestParams
import org.eclipse.lsp4j.TextDocumentIdentifier
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture

/**
 *
 * Concrete implementation of `FoldingProvider`
 * @see FoldingProvider
 *
 * @author itSMcodez
 */
class LspFoldingProvider(
    private val language: LspLanguage
) : FoldingProvider {

    override fun requestFoldingRegions(documentUri: String): CompletableFuture<List<FoldingRegion>> {
        val editor = language.editor
        val server = editor.languageServerWrapper.getServer() ?: return completedFuture(emptyList())
        val capability = editor.languageServerWrapper.getServerCapabilities()?.foldingRangeProvider ?: return completedFuture(emptyList())

        val params = FoldingRangeRequestParams().apply {
            textDocument = TextDocumentIdentifier(documentUri)
        }

        if (capability.left == false || capability.right == null) {
            return completedFuture(emptyList())
        } else Log.d(this.javaClass.simpleName, "Lsp supports code folding!")

        return server.textDocumentService.foldingRange(params).thenApply { ranges ->
            ranges.mapNotNull { range ->
                val start = range.startLine
                val end = range.endLine
                val startCharacter = range.startCharacter
                val endCharacter = range.endCharacter
                FoldingRegion(start, startCharacter, end, endCharacter)
            }
        }
    }
}
