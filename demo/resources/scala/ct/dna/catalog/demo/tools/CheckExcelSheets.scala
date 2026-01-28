package ct.dna.catalog.demo.tools

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream

object CheckExcelSheets {
  def main(args: Array[String]): Unit = {
    val excelPath = args.headOption.getOrElse("test-data-typed/sales/2024/q1/transactions.xlsx")

    println(s"Checking sheets in: $excelPath\n")

    val fis      = new FileInputStream(excelPath)
    val workbook = new XSSFWorkbook(fis)

    try {
      val numberOfSheets = workbook.getNumberOfSheets
      println(s"Number of sheets: $numberOfSheets")

      for (i <- 0 until numberOfSheets) {
        val sheet     = workbook.getSheetAt(i)
        val sheetName = sheet.getSheetName
        val rowCount  = sheet.getPhysicalNumberOfRows
        println(s"\nSheet ${i + 1}: '$sheetName' - $rowCount rows")

        // Show first few rows
        if (rowCount > 0) {
          val headerRow = sheet.getRow(0)
          if (headerRow != null) {
            print("  Headers: ")
            for (j <- 0 until headerRow.getPhysicalNumberOfCells) {
              print(s"${headerRow.getCell(j)} ")
            }
            println()
          }

          // Show first data row
          if (rowCount > 1) {
            val dataRow = sheet.getRow(1)
            if (dataRow != null) {
              print("  First row: ")
              for (j <- 0 until dataRow.getPhysicalNumberOfCells) {
                print(s"${dataRow.getCell(j)} ")
              }
              println()
            }
          }
        }
      }

      // Check for expected sheets based on file path
      if (excelPath.contains("q1")) {
        if (numberOfSheets == 3 &&
          workbook.getSheet("january") != null &&
          workbook.getSheet("february") != null &&
          workbook.getSheet("march") != null) {
          println("\n✅ SUCCESS: All three Q1 sheets (january, february, march) are present!")
        } else {
          println("\n❌ FAILED: Not all expected Q1 sheets found")
        }
      } else if (excelPath.contains("q2")) {
        if (numberOfSheets == 3 &&
          workbook.getSheet("april") != null &&
          workbook.getSheet("may") != null &&
          workbook.getSheet("june") != null) {
          println("\n✅ SUCCESS: All three Q2 sheets (april, may, june) are present!")
        } else {
          println("\n❌ FAILED: Not all expected Q2 sheets found")
        }
      }

    } finally {
      workbook.close()
      fis.close()
    }
  }
}
