package com.codigo.tryWithResources;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LectorArchivoDemo {

    public static String leerPrimeraLinea(String ruta) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {

            return br.readLine();

        }
    }
}
