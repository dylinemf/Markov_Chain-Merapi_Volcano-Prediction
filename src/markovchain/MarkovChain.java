package markovchain;

import java.io.BufferedInputStream;
import java.io.File;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/*
Anggota kelompok:
175314110 CLAUDHEA ZITA LEGINA
185314058 YOHANES DANI PRATAMA RAHADIANTO
185314114 AGUSTINA BUDI STEVANI
185314120 NOVIYAN IMAM ARIFIN
185314125 DYLINE MELYNEA FERNANDEZ
 */
public class MarkovChain {

    //jumlah kolom dan baris untuk matrix awal yaitu matrix saat ini
    int r1 = 1, c1 = 3, numOfRows;
    //jumlah kolom dan baris untuk matrix transisi
    int r2 = 3, c2 = 3;
    //matrix transisi
//    static double[][] secondMatrix = {{0.454545455, 0.363636364, 0.181818182},
//    {0.5, 0, 0.5},
//    {1, 0, 0}
//    };
    String status_di_hari[];
    //pembuatan array untuk matrix probabilitas awal yaitu matrix sesuai kondisi sekarang
    double[][] matrixProb = new double[1][3];
    double[][] matrixT;
    //pembuatan matrix untuk hasil akhir
    double[][] prob = new double[1][3];
    //n-hari setelah hari ini yang akan diinputkan oleh user  untuk mengetahui
    //probabilitas status gunung merapi setelah n hari
    int n;
    Scanner myObj = new Scanner(System.in);

    public String ReadStringCellData(String value, Workbook wb, int vRow, int vColumn) {
        Sheet sheet = wb.getSheetAt(0);   //getting the XSSFSheet object at given index  
        Row row = sheet.getRow(vRow); //returns the logical row  
        Cell cell = row.getCell(vColumn); //getting the cell representing the given column  
        value = cell.getStringCellValue();    //getting cell value  
        System.out.println("value: " + value);
        return value;               //returns the cell value  
    }

    public void readHari(File fileexcel) {
        String value = "";          //variable for storing the cell value  
        Workbook wb = null;           //initialize Workbook null  
        try {
            //reading data from a file in the form of bytes  
//            String path = new File(getClass().getResource("/data").getFile()).getAbsolutePath() + "\\" + file.getName();            
            FileInputStream fis = new FileInputStream(fileexcel);
            //constructs an XSSFWorkbook object, by buffering the whole stream into the memory  
            wb = new XSSFWorkbook(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Sheet sheet = wb.getSheetAt(0);   //getting the XSSFSheet object at given index
        numOfRows = sheet.getLastRowNum();
        MarkovChain rc = new MarkovChain();
        System.out.println(rc);
        status_di_hari = new String[numOfRows];
        for (int i = 0; i < status_di_hari.length; i++) {
            System.out.println("posisi: " + i + ", 2");
            status_di_hari[i] = rc.ReadStringCellData(value, wb, (i + 1), 1);
        }
    }

    public void main(String[] args) {
//        readHari();
        System.out.println(Arrays.deepToString(matriksTransisi()));
        prediksi();
        for (int i = 0; i < n; i++) {
            matrixProb = operasiPerhitungan();
        }
        hasilPerhitungan();

    }

    //perkalian matrix
    double[][] operasiPerhitungan() {
        //Mutliplying Two matrices
        double[][] product = new double[r1][c2];
        for (int i = 0; i < r1; i++) {
            for (int j = 0; j < c2; j++) {
                for (int k = 0; k < c1; k++) {
                    product[i][j] += matrixProb[i][k] * matrixT[k][j];
                }
            }
        }
        return product;
    }

    //inisialisasi status matrix awal sesuai status gunung saat ini
    void initializeMatrix(String input) {
        switch (input.toLowerCase()) {
            case "normal":
                matrixProb[0][0] = 0;
                matrixProb[0][1] = 0;
                matrixProb[0][2] = 1;
                break;
            case "siaga":
                matrixProb[0][0] = 0;
                matrixProb[0][1] = 1;
                matrixProb[0][2] = 0;
                break;
            case "waspada":
                matrixProb[0][0] = 1;
                matrixProb[0][1] = 0;
                matrixProb[0][2] = 0;
                break;
        }
    }

    double[][] matriksTransisi() {
        LinkedList list_state = new LinkedList();
        double[] count_each_state;
        for (int i = 0; i < numOfRows; i++) {
            if (!list_state.contains(status_di_hari[i])) {
                list_state.add(status_di_hari[i]);
            }
        }

        int num_of_state = list_state.size();
        count_each_state = new double[num_of_state];

        for (int i = 0; i < count_each_state.length; i++) {
            int count = 0;
            for (int j = 0; j < numOfRows-1; j++) {
                if (list_state.get(i).equals(status_di_hari[j])) {
                    count++;
                }
            }
//            if (i == (num_of_state - 1)) {
//                count_each_state[i] = (count - 1);
//            } else {
                count_each_state[i] = count;
//            }
        }

        matrixT = new double[num_of_state][num_of_state];

        for (int j = 0; j < num_of_state; j++) {

            for (int h = 0; h < num_of_state; h++) {
                double count = 0;
                for (int i = 0; i < numOfRows; i++) {
                    if (i != (status_di_hari.length - 1)) {
                        if (status_di_hari[i] == list_state.get(j)
                                && status_di_hari[i + 1] == list_state.get(h)) {
                            count++;
                        }
                    }
                }
                matrixT[j][h] = (count / count_each_state[j]);
            }
        }
        return matrixT;
    }

//output hasil probabilitas status gunung setelah n hari
    void hasilPerhitungan() {
        System.out.println("Jadi probabilitas status gunung setelah : " + n + " Hari adalah : ");
        System.out.println("Waspada : " + matrixProb[0][0]);
        System.out.println("Siaga : " + matrixProb[0][1]);
        System.out.println("Normal : " + matrixProb[0][2]);
    }

    //untuk menerima input dari user mengenai status, dan rentang hari
    void prediksi() {
        System.out.println("Status gunung merapi terdapat 3 status yaitu : waspada, siaga, dan normal");
        System.out.println("Silahkan pilih status gunung merapi saaat ini");
        initializeMatrix(myObj.nextLine());
        System.out.println("masukan rentang hari setelah hari ini untuk diketahui probabilitas statusnya :");
        n = myObj.nextInt();
    }
}
