/**
 * INF1416 - Segurança da Informação - Trabalho 3: DigestCalculator
 * Prof.: Anderson Oliveira da Silva 
 * 
 * Gustavo Barros Marchesan - 1521500
 * Daniela Brazão Maksoud - 2111121
 * 
 */

// Pacotes importados
import java.security.*;
import javax.crypto.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;
import java.util.Scanner;
import java.util.*;

class Arquivo{
    String file_name;  
    String hash;
    String status = "OK";
    boolean newLine;
}  

public class DigestCalculator {

    private static String getDigest(String algorithm) {
        switch (algorithm) {
            case "MD5":
                return algorithm;
            case "SHA1":
                return "SHA-1";
            case "SHA256":
                return "SHA-256";
            case "SHA512":
                return "SHA-512";
            default:
                System.err.println("\nTipo de Digest Inválido.\n");
                System.exit(1);
                return null;
        }
    }

    private static byte[] getDigest(byte[] contentBytes, String digestType) {
        try {
            MessageDigest msg = MessageDigest.getInstance(digestType);
            msg.update(contentBytes);
            return msg.digest();
        } catch(Exception e) {
            System.err.println(e.getMessage()); 
            System.exit(1);
            return null;
        }
    }
	
	private static String convertByteToString(byte[] fileDigest) {
		String digest = "";
		
		for(int i = 0; i < fileDigest.length; i++)
			digest = digest + String.format("%02X", fileDigest[i]);
		
		return digest;
	}

    private static Arquivo getArquivo(File file, String digestType) {
        Arquivo arquivo = new Arquivo();
        arquivo.file_name = file.getName();

        System.out.println(file.getAbsolutePath());

        try {
            String path = file.getAbsolutePath();
            byte[] file_content = Files.readAllBytes(Paths.get(path));
            byte[] digest = getDigest(file_content, digestType);
            arquivo.hash = convertByteToString(digest);

            System.out.println(arquivo.hash);
        } catch(Exception e) {
            System.err.println(e.getMessage()); 
            System.exit(1);
        }

        return arquivo;
    }

    private static ArrayList<Arquivo> getArquivosDaPasta(String folderPath, String digestType) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        ArrayList<Arquivo> arquivos = new ArrayList<>();

        for(int i = 0; i < files.length; i++)
            arquivos.add(getArquivo(files[i], digestType));

        System.out.println("");

        return arquivos;
    }

    private static Arquivo getArquivoLine(String line, String digestType) {
        String[] text = line.split(" ");

        Arquivo arquivo = new Arquivo();
        arquivo.file_name = text[0];

        for(int i = 1; i < text.length; i = i + 2) {
            if(text[i].equals(digestType)) {
                arquivo.hash = text[i+1];
                break;
            }
        }

        return arquivo;
    }

    private static ArrayList<Arquivo> getArquivosDaLista(String listPath, String digestType) {
        File list = new File(listPath);
        String path = list.getAbsolutePath();
        List<String> lines = null;

        try {
            lines = Files.readAllLines(Paths.get(path));
        } catch(Exception e) {
            System.err.println(e.getMessage()); 
            System.exit(1);
        }

        ArrayList<Arquivo> arquivos = new ArrayList<>();

        for(String line : lines)
            arquivos.add(getArquivoLine(line, digestType));

        return arquivos;
    }

    public static void printInOrder(ArrayList<Arquivo> arquivosDaPasta, ArrayList<Arquivo> arquivosDaLista, String digestTypeNoDash) {
        // printa o que está na lista
        for(Arquivo arq1 : arquivosDaLista) {
            for(Arquivo arq2 : arquivosDaPasta) {
                if (arq1.file_name.equals(arq2.file_name))
                    System.out.println(arq2.file_name + " " + digestTypeNoDash + " " + arq2.hash + " (" + arq2.status + ")");
            }
        }

        // printa o que não está na lista
        for(Arquivo arq1 : arquivosDaPasta) {
            boolean found = false;

            for(Arquivo arq2 : arquivosDaLista) {
                if (arq1.file_name.equals(arq2.file_name))
                    found = true;
            }

            if(!found)
                System.out.println(arq1.file_name + " " + digestTypeNoDash + " " + arq1.hash + " (" + arq1.status + ")");
        }

        System.out.println("");
    }

    public static void addToEndOfFile(Path path, Arquivo arquivo, String digestTypeNoDash) {
        try {
            String line = arquivo.file_name + " " + digestTypeNoDash + " " + arquivo.hash + "\n";
            Files.write(path, line.getBytes(), StandardOpenOption.APPEND);
        } catch(Exception e) {
            System.err.println(e.getMessage()); 
            System.exit(1);
        }
    }

    public static void addToEndOfLine(Path path, Arquivo arquivo, String digestTypeNoDash) {
        try {
            List<String> lines = Files.readAllLines(path);

            for(int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] text = line.split(" ");
                
                if(arquivo.file_name.equals(text[0])) {
                    lines.set(i, line + " " + digestTypeNoDash + " " + arquivo.hash);
                    break;
                }
            }

            Files.write(path, lines, StandardCharsets.UTF_8, StandardOpenOption.WRITE);
        } catch(Exception e) {
            System.err.println(e.getMessage()); 
            System.exit(1);
        }
    }

    public static void addToFile(String listPath, Arquivo arquivo, String digestTypeNoDash) {
        File list = new File(listPath);
        Path path = Paths.get(list.getAbsolutePath());

        if(arquivo.newLine)
            addToEndOfFile(path, arquivo, digestTypeNoDash);
        else
            addToEndOfLine(path, arquivo, digestTypeNoDash);
    }

    public static void main (String[] args) throws Exception {

        // Verifica o número de argumentos fornecidos pelo usuário
        if (args.length != 3) {
            System.err.println("Usage: java DigestCalculator <digest_type> <path_file_with_digest_list> <path_folder_with_files>");
            System.exit(1);
        }

        String digestType = getDigest(args[0]);
        String digestTypeNoDash = args[0];
        String pathFileWithDigestList = args[1];
        String pathFolderWithFiles = args[2];

        System.out.println("Digest with dash: " + digestType);
        System.out.println("Digest without dash: " + digestTypeNoDash);
        System.out.println("Digest file path: " + pathFileWithDigestList);
        System.out.println("Digest folder path: " + pathFolderWithFiles);
        System.out.println("");

        ArrayList<Arquivo> arquivosDaPasta = getArquivosDaPasta(pathFolderWithFiles, digestType);
        ArrayList<Arquivo> arquivosDaLista = getArquivosDaLista(pathFileWithDigestList, digestTypeNoDash);

        for(Arquivo arq1 : arquivosDaPasta) {
            for(Arquivo arq2 : arquivosDaPasta) {
                if(arq1 == arq2)
                    continue;
                
                if(arq1.hash.equals(arq2.hash)) {
                    arq1.status = "COLLISION";
                    arq2.status = "COLLISION";
                    break;
                }
            }
        }

        for(Arquivo arq1 : arquivosDaPasta) {
            if(arq1.status.equals("COLLISION"))
                continue;

            boolean onList = false;

            for(Arquivo arq2 : arquivosDaLista) {
                if(arq1.file_name.equals(arq2.file_name)) {
                    onList = true;

                    if(arq2.hash == null) {
                        arq1.status = "NOT FOUND";
                        arq1.newLine = false;
                        continue;
                    }

                    if(!arq1.hash.equals(arq2.hash))
                        arq1.status = "NOT OK";
                } else if(arq1.hash.equals(arq2.hash)){
                    arq1.status = "COLLISION";
                    break;
                }
            }

            if(onList == false && !arq1.status.equals("COLLISION")) {
                arq1.status = "NOT FOUND";
                arq1.newLine = true;
            }
        }

        printInOrder(arquivosDaPasta, arquivosDaLista, digestTypeNoDash);

        for(Arquivo arq1 : arquivosDaPasta) {
            if(arq1.status.equals("NOT FOUND"))
                addToFile(pathFileWithDigestList, arq1, digestTypeNoDash);
        }
    }
}