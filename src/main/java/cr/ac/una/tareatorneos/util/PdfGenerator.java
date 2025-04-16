package cr.ac.una.tareatorneos.util;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import cr.ac.una.tareatorneos.model.TeamTournamentStats;

import java.io.File;
import java.time.LocalDate;

public class PdfGenerator {

    public static void crearCarneCampeon(String equipoNombre, String torneoNombre, String deporte,
                                         String rutaImagen, TeamTournamentStats.TournamentStat torneoStats) {
        try {
            // 🗂 Crear directorio
            String dir = "certificados";
            new File(dir).mkdirs();

            // 📄 Definir nombre PDF
            String pdfPath = dir + "/certificado_" + torneoNombre.replaceAll("\\s+", "_") + "_" + equipoNombre.replaceAll("\\s+", "_") + ".pdf";
            PdfWriter writer = new PdfWriter(pdfPath);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            // 🏆 Título dorado
            Paragraph titulo = new Paragraph("🏆 Certificado de Campeón 🏆")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(26)
                    .setBold()
                    .setFontColor(ColorConstants.ORANGE);
            doc.add(titulo);
            doc.add(new Paragraph("\n"));

            // 🖼 Logo institucional arriba
            try {
                ImageData logoImg = ImageDataFactory.create(PdfGenerator.class.getResource("/cr/ac/una/tareatorneos/resources/LogoUNArojo.png"));
                Image logo = new Image(logoImg).scaleToFit(50, 50);
                logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
                doc.add(logo);
            } catch (Exception e) {
                System.out.println("⚠ No se pudo cargar el logo institucional");
            }

            doc.add(new Paragraph("\n"));

            // 🧍‍♂️ Imagen del equipo con borde dorado
            if (rutaImagen != null && new File(rutaImagen).exists()) {
                ImageData imageData = ImageDataFactory.create(rutaImagen);
                Image img = new Image(imageData).scaleToFit(130, 130)
                        .setBorder(new SolidBorder(ColorConstants.ORANGE, 3))
                        .setHorizontalAlignment(HorizontalAlignment.CENTER);
                doc.add(img);
            }

            doc.add(new Paragraph("\n"));

            // 📝 Datos del torneo (validados)
            doc.add(new Paragraph("Equipo: " + validar(equipoNombre)).setBold().setFontSize(14));
            doc.add(new Paragraph("Torneo: " + validar(torneoNombre)));
            doc.add(new Paragraph("Deporte: " + validar(deporte)));
            doc.add(new Paragraph("Fecha de victoria: " + LocalDate.now()));
            doc.add(new Paragraph("Resultado del torneo: " + validar(torneoStats.getResultadoTorneo())));

            doc.add(new Paragraph("\n🧾 Desempeño del equipo:\n").setBold());

            // 📊 Tabla de resultados
            Table table = new Table(4).useAllAvailableWidth();
            table.addHeaderCell(new Cell().add(new Paragraph("Rival")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Anotaciones")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("En Contra")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Resultado")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            for (var p : torneoStats.getPartidos()) {
                table.addCell(new Cell().add(new Paragraph(validar(p.getRival()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(p.getAnotaciones()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(p.getAnotacionesEnContra()))));
                table.addCell(new Cell().add(new Paragraph(validar(p.getResultadoReal()))));
            }

            doc.add(table);
            doc.add(new Paragraph("\n"));

            // ✅ Validación oficial
            doc.add(new Paragraph("Este certificado es emitido por la Universidad Nacional de Costa Rica, Sede Región Brunca PZ, como reconocimiento oficial al equipo campeón.").setItalic().setFontSize(12).setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("\n\n"));

            // ✍️ Firma visual
            try {
                ImageData firmaImg = ImageDataFactory.create(PdfGenerator.class.getResource("/cr/ac/una/tareatorneos/resources/SigniatureIcon.png"));
                Image firma = new Image(firmaImg).scaleToFit(150, 80);
                firma.setHorizontalAlignment(HorizontalAlignment.CENTER);
                doc.add(firma);
            } catch (Exception e) {
                System.out.println("⚠ No se pudo cargar imagen de firma.");
            }

            doc.add(new Paragraph("__________________________").setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("Dirección de Deportes y Recreación").setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("Universidad Nacional - PZ").setTextAlignment(TextAlignment.CENTER));

            doc.close();

            // 🎉 Abrir automáticamente
            java.awt.Desktop.getDesktop().open(new File(pdfPath));
            System.out.println("✅ Certificado generado en: " + pdfPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Función auxiliar para evitar valores nulos
    private static String validar(String texto) {
        return (texto == null || texto.isBlank()) ? "N/A" : texto;
    }


}
