package ru.mit.spbau.hanabi

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.shockwave.pdfium.PdfDocument


class RulesActivity : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener {

    private val TAG = RulesActivity::class.java.simpleName
    private val RULES_PDF = "hanabi.pdf"
    private var pdfView: PDFView? = null
    private var pageNumber = 0
    private var pdfFileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)

        pdfView = findViewById(R.id.pdfView)
        displayPdfFromAsset(RULES_PDF)
    }

    private fun displayPdfFromAsset(pdfFileName: String) {
        pageNumber = 0
        this.pdfFileName = pdfFileName
        pdfView?.fromAsset(pdfFileName)
                ?.defaultPage(pageNumber)
                ?.enableSwipe(true)
                ?.swipeHorizontal(false)
                ?.onPageChange(this)
                ?.enableAnnotationRendering(true)
                ?.onLoad(this)
                ?.scrollHandle(DefaultScrollHandle(this))
                ?.load()
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        pageNumber = page
        title = String.format("%s %s / %s", pdfFileName, page + 1, pageCount)
    }

    override fun loadComplete(nbPages: Int) {
        printBookmarksTree(pdfView!!.tableOfContents, "-")
    }

    private fun printBookmarksTree(tree: List<PdfDocument.Bookmark>, sep: String) {
        for (b in tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.title, b.pageIdx))

            if (b.hasChildren()) {
                printBookmarksTree(b.children, sep + "-")
            }
        }
    }

}
