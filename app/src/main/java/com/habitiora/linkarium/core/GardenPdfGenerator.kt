package com.habitiora.linkarium.core

// GardenPdfGenerator.kt
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.habitiora.linkarium.domain.model.LinkGardenWithSeeds
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GardenPdfGenerator @Inject constructor() {

    private object PdfConstants {
        const val PAGE_WIDTH = 595
        const val PAGE_HEIGHT = 842
        const val MARGIN = 40f
        const val MAX_CONTENT_HEIGHT = PAGE_HEIGHT - MARGIN * 2
        const val TITLE_TEXT_SIZE = 16f
        const val BODY_TEXT_SIZE = 12f
        const val LINE_SPACING_LARGE = 30f
        const val LINE_SPACING_MEDIUM = 22f
        const val LINE_SPACING_SMALL = 10f
        const val INDENT = 20f
    }

    fun generate(gardens: List<LinkGardenWithSeeds>, outputStream: OutputStream) {
        val pdfDocument = PdfDocument()
        val titlePaint = Paint().apply {
            textSize = PdfConstants.TITLE_TEXT_SIZE
            isFakeBoldText = true
        }
        val bodyPaint = Paint().apply {
            textSize = PdfConstants.BODY_TEXT_SIZE
        }

        var currentPage: PdfDocument.Page? = null
        var yPosition = PdfConstants.MARGIN

        fun startNewPage() {
            currentPage?.let { pdfDocument.finishPage(it) }
            val pageInfo = PdfDocument.PageInfo.Builder(PdfConstants.PAGE_WIDTH, PdfConstants.PAGE_HEIGHT, pdfDocument.pages.size + 1).create()
            currentPage = pdfDocument.startPage(pageInfo)
            yPosition = PdfConstants.MARGIN
        }

        startNewPage() // Iniciar la primera página

        gardens.forEach { garden ->
            // Revisa si hay espacio para el siguiente jardín
            if (yPosition > PdfConstants.MAX_CONTENT_HEIGHT - 100) { // Un estimado de espacio mínimo
                startNewPage()
            }

            // Dibuja el nombre del jardín
            currentPage!!.canvas.drawText("Name: ${garden.garden.name}", PdfConstants.MARGIN, yPosition, titlePaint)
            yPosition += PdfConstants.LINE_SPACING_LARGE

            // Dibuja las notas del jardín
            currentPage.canvas.drawText("Notes: ${garden.garden.description}", PdfConstants.MARGIN, yPosition, bodyPaint)
            yPosition += PdfConstants.LINE_SPACING_LARGE

            // Dibuja las semillas
            garden.seeds.forEach { seed ->
                if (yPosition > PdfConstants.MAX_CONTENT_HEIGHT) {
                    startNewPage()
                }

                currentPage.canvas.drawText(
                    "• ${seed.name.ifBlank { "Sin nombre" }}",
                    PdfConstants.MARGIN + PdfConstants.INDENT,
                    yPosition,
                    bodyPaint
                )
                yPosition += PdfConstants.LINE_SPACING_MEDIUM

                seed.links.forEach { link ->
                    currentPage.canvas.drawText(
                        "- $link",
                        PdfConstants.MARGIN + PdfConstants.INDENT + PdfConstants.INDENT,
                        yPosition,
                        bodyPaint
                    )
                    yPosition += PdfConstants.LINE_SPACING_SMALL
                }

                // Puedes agregar más detalles de la semilla aquí...
            }

            yPosition += PdfConstants.LINE_SPACING_LARGE // Espacio entre jardines
        }

        pdfDocument.finishPage(currentPage)
        pdfDocument.writeTo(outputStream)
        pdfDocument.close()
    }
}