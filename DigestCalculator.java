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
import java.math.BigInteger;
import java.util.Scanner;
import java.util.*;

class Arquivo{
    String file_name;  
    String md5; 
    String sha1;
    String sha256; 
    String sha512;
}  

public class DigestCalculator {

    public static void main (String[] args) throws Exception {

        // Verifica o número de argumentos fornecidos pelo usuário
        if (args.length != 3) {
            System.err.println("Usage: java DigestCalculatorTest <digest_type> <path_file_with_digest_list> <path_folder_with_files>");
            System.exit(1);
        }

        String digestType = args[0];
        String pathFileWithDigestList = args[1];
        String pathFolderWithFiles = args[2];

        // System.out.println("Argumentos passados:");
        // System.out.println(digestType);
        // System.out.println(pathFileWithDigestList);
        // System.out.println(pathFolderWithFiles);

        // Verifica o tipo do digest a ser calculado
        switch (digestType) {
            case "MD5":
                break;
            case "SHA1":
                digestType = "SHA-1";
                break;
            case "SHA256":
                digestType = "SHA-256";
                break;
            case "SHA512":
                digestType = "SHA-512";
                // System.out.println(digestType);
                break;
            default:
                System.err.println("");
                System.err.println("Tipo de Digest Inválido.");
                System.err.println("");
                System.exit(1);
        }

        // Lê todos os arquivos dentro da pasta fornecida pelo usuário
        try { 

			// Cria um objeto File
			File f = new File(pathFolderWithFiles); 

			// Salva os nomes de todos os arquivos dentro 
			// do diretório informado 
			File[] files = f.listFiles(); 

			System.out.println("");
            System.out.println("Arquivos existentes em ");
            System.out.print(pathFolderWithFiles);
            System.out.println(":");
            System.out.println("");

            // Incializa vetor de arquivos
            Arquivo arq[] = new Arquivo[files.length];  

			// Exibe os nomes de todos os arquivos dentro do diretório informado 
			for (int i = 0; i < files.length; i++) {
                System.out.print("Nome do arquivo: "); 
                String file_name = files[i].getName();
                System.out.print(file_name);
                System.out.println("");
                //System.out.println(pathFolderWithFiles+"/"+files[i].getName());
                // Lê e exibe conteúdo de arquivo
                String path_name = pathFolderWithFiles+"/"+file_name;
                String file_content = Files.readString(Paths.get(path_name));
                System.out.println("");
                System.out.println("Conteúdo do arquivo:"); 
                System.out.println("");
                System.out.println(file_content);
                System.out.println("");
                System.out.println("-----------------------------------------------------------------");
                System.out.println("");
                // Calcula e exibe digest do arquivo
                MessageDigest msg = MessageDigest.getInstance(digestType);
                msg.update(file_content.getBytes());
                byte[] digest = msg.digest();
                //String.format("%1$040x", new BigInteger(1, digest));
                System.out.print("Digest do conteúdo de ");
                System.out.print(file_name);
                System.out.print(":");
                System.out.println("\n");
                String final_digest = String.format("%1$040x", new BigInteger(1, digest));
                System.out.println(final_digest);
                System.out.println("");
                System.out.println("-----------------------------------------------------------------");
                System.out.println("");
                // Inicializa objeto do arquivo
                arq[i] = new Arquivo();
                arq[i].md5 = "";
                arq[i].sha1 = "";
                arq[i].sha256 = "";
                arq[i].sha512 = "";
                // Preenche e exibe objeto do arquivo
                System.out.println("Arquivo salvo:");
                arq[i].file_name = file_name;
                System.out.println(file_name);
                if (digestType == "MD5") {
                    arq[i].md5 = final_digest;
                    System.out.println(arq[i].md5);
                }
                if (digestType == "SHA-1") {
                    arq[i].sha1 = final_digest;
                    System.out.println(arq[i].sha1);
                }
                if (digestType == "SHA-256") {
                    arq[i].sha256 = final_digest;
                    System.out.println(arq[i].sha256);
                }
                if (digestType == "SHA-512") {
                    arq[i].sha512 = final_digest;
                    System.out.println(arq[i].sha512);
                }
                System.out.println("");
                System.out.println("-----------------------------------------------------------------");
                System.out.println("");
			}

            try  
            {  
                // Cria lista de listas
                List<List<String>> listOfLists = new ArrayList<>();

                // Abre arquivo para leitura 
                FileInputStream list_file = new FileInputStream(pathFileWithDigestList);       
                Scanner sc = new Scanner(list_file);  // Arquivo a ser varrido 

                // Retorna true se tiver uma próxima linha a ser lida  
                while(sc.hasNextLine())  
                {  
                    // System.out.println(sc.nextLine()); // Retorna a linha que foi pulada  
                    String input = sc.nextLine();

                    // Cria lista interna 
                    List<String> innerList = new ArrayList<>();
                    
                    // Adiciona elementos à lista interna
                    String[] list_file_content = input.split(" ");

                    // System.out.println(list_file_content[0]);
                    // System.out.println(list_file_content[1]);
                    // System.out.println(list_file_content[2]);
                    // System.out.println(list_file_content.length);

                    for (int i = 0; i < list_file_content.length; i++) {
                        innerList.add(list_file_content[i]);
                    }

                    // Salva lista interna na lista das listas
                    listOfLists.add(innerList);
                }  
                sc.close(); // Finaliza o escaneamento  

                System.out.println(listOfLists);
            }  
            catch(IOException e)  
            {  
                e.printStackTrace();  
            }

            // Varre os dados dos arquivos salvos e compara com os da lista
            for (int i = 0; i < arq.length; i++) {
                // System.out.println(arq[i].file_name);
                // System.out.println(arq[i].md5);
                // System.out.println(arq[i].sha1);
                // System.out.println(arq[i].sha256);
                // System.out.println(arq[i].sha512);
                // System.out.println(" ");
                // if (list_file_content[0] == "MD5") {

                // }
                // if (list_file_content[0] == "SHA-1") {

                // }
                // if (list_file_content[0] == "SHA-256") {

                // }
                // if (list_file_content[0] == "MD5") {

                // }
            }

            System.out.println("");
		} 
		catch (Exception e) { 
			System.err.println(e.getMessage()); 
		}
    }
}