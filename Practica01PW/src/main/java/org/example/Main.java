package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingresa aqui la URL a revisar: ");
        String url = scanner.nextLine();

        if (!url.startsWith("http") && !url.startsWith("https")) {
            url = "http://" + url;
        }

        StringBuilder contenido = new StringBuilder();

        try {
            URL urlObj = new URL(url);
            HttpURLConnection conexion = (HttpURLConnection) urlObj.openConnection();
            conexion.setInstanceFollowRedirects(true);
            conexion.setRequestMethod("GET");
            conexion.connect();
            //TIPO DE CONTENIDO
            String contentType = conexion.getContentType();
            System.out.printf("El URL subido es un documento de tipo: %s\n", contentType);

            if(contentType.contains("text/html")) {
                //CONTADOR DE LINEAS
                BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String linea;
                int contador = 0;
                while ((linea = br.readLine()) != null) {
                    contenido.append(linea);
                    contador++;
                }
                br.close();

                System.out.printf("Las lineas que tiene el HTML son: %s\n", contador);

               //CONTADOR DE PARRAFOS
                Document documento = Jsoup.parse(contenido.toString());
                Elements para = documento.select("p");
                int paraCount = para.size();
                System.out.printf("La cantidad de parrafos (<p>) que tiene el HTML es: %s\n", paraCount);
                //CONTADOR DE IMAGENES
                int contadorImg = 0;
                for(Element prgh : para){
                    contadorImg += prgh.select("img").size();
                }

                //CONTADORES DE FORMS GET Y POSTS
                Elements formularios = documento.select("form");
                int formulariosPosts = 0;
                int formulariosGets = 0;
                for(Element formulario : formularios){
                    String formularioMetodo = formulario.attr("Method").toUpperCase();
                    if(formularioMetodo.equals("POST")) {
                        formulariosPosts++;
                    }else if(formularioMetodo.equals("GET")) {
                        formulariosGets++;
                    }

                    //Mostrar Inputs y sus tipos
                    Elements inputs = documento.select("input");
                    System.out.printf("El tipo de  input es: %s con %d campos\n",formularioMetodo, inputs.size());
                    for(Element input : inputs){
                        System.out.printf("Tipo de Input y Nombre: %s, %d", input.attr("type"), input.attr("name"));
                    }

                    if(formularioMetodo.equals("POST")) {
                        mensajePost(formulario);
                    }
                }
                



            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void mensajePost(Element formulario) {

        try {

            String accion = formulario.attr("Action");
            if(accion.isEmpty()) {
                accion = "/";
            }

            URL urlObj = new URL(accion);
            HttpURLConnection conexion = (HttpURLConnection) urlObj.openConnection();
            conexion.setRequestMethod("POST");
            conexion.setDoOutput(true);

            String parametros = "Materia=" + URLEncoder.encode("PrimeraPractica", "UTF-8");
            conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conexion.setRequestProperty("matricula-id", "tuMatriculaOTuId");
            try (OutputStream os = conexion.getOutputStream()) {
                byte[] bytes = parametros.getBytes("UTF-8");
                os.write(bytes, 0, bytes.length);
            }

            int codigoRespuesta = conexion.getResponseCode();
            System.out.println("Respuesta: " + codigoRespuesta);

            BufferedReader in = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            String linea;
            StringBuilder contenido = new StringBuilder();
            while ((linea = in.readLine()) != null) {
                contenido.append(linea);
            } in.close();

            System.out.println("Respuesta mandada desde el servidor: ");
            System.out.println(contenido.toString());



        }catch (IOException e){
            e.printStackTrace();

        }
    }
    private static  void log(String msg, String rescp){
        System.out.println(String.format(msg, rescp));}
}
