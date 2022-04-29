import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelWriter {

    XSSFWorkbook workbook = new XSSFWorkbook();
    XSSFSheet sheet = workbook.createSheet("Data");
    String path = "";
    public ExcelWriter(String pathToWriteExcel) {
        this.path=pathToWriteExcel;
    }

    public void write(List<Error> errors) {
        int rownum = 0;
        Row row = sheet.createRow(rownum++);
        int maxErrors = 0;
        row.createCell(0).setCellValue("Date");
        row.createCell(1).setCellValue("Times");
        for (Error error :errors){
            row = sheet.createRow(rownum++);
            row.createCell(0).setCellValue(error.getDate());
            row.createCell(1).setCellValue(error.getTimes());
            if (error.getTimes()>maxErrors){
                maxErrors = error.getTimes();
            }
        }



        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 3, 0,  errors.size(),maxErrors*2);
        if(errors.size()<20||maxErrors<6){
            anchor = drawing.createAnchor(0, 0, 0, 0, 3, 0,  errors.size()*2,errors.size()*2);
        }

        XSSFChart chart = drawing.createChart(anchor);

        chart.setTitleText("Data graph");
        chart.setTitleOverlay(false);

        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("Dates");
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Times");

        XDDFDataSource<String> titles = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                new CellRangeAddress(1, errors.size(), 0, 0));

        XDDFNumericalDataSource<Double> errorsTimes = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                new CellRangeAddress(1, errors.size(), 1, 1));

//        XDDFNumericalDataSource<Double> population = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
//                new CellRangeAddress(2, 2, 0, 6));

        XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);

//        XDDFLineChartData.Series series1 = (XDDFLineChartData.Series) data.addSeries(countries, area);
//        series1.setTitle("Date", null);
//        series1.setSmooth(false);
//        series1.setMarkerStyle(MarkerStyle.STAR);

        XDDFLineChartData.Series series2 = (XDDFLineChartData.Series) data.addSeries(titles, errorsTimes);
        series2.setTitle("Errors", null);
        series2.setSmooth(false);
        series2.setMarkerSize((short)6);
        series2.setMarkerStyle(MarkerStyle.CIRCLE);
        String color = "#0C1111";
        lineSeriesColor(series2, XDDFColor.from(hex2Rgb(color)));
        chart.plot(data);

        try
        {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File(path));
            workbook.write(out);
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void lineSeriesColor(XDDFChartData.Series series, XDDFColor color) {
        XDDFSolidFillProperties fill = new XDDFSolidFillProperties(color);
        XDDFLineProperties line = new XDDFLineProperties();
        line.setFillProperties(fill);
        XDDFShapeProperties properties = series.getShapeProperties();
        if (properties == null) {
            properties = new XDDFShapeProperties();
        }
        properties.setLineProperties(line);
        series.setShapeProperties(properties);
    }

    private byte[] hex2Rgb(String colorStr) {
        int r = Integer.valueOf(colorStr.substring(1, 3), 16);
        int g = Integer.valueOf(colorStr.substring(3, 5), 16);
        int b = Integer.valueOf(colorStr.substring(5, 7), 16);
        return new byte[]{(byte) r, (byte) g, (byte) b};
    }

}
