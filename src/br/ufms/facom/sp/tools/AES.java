package br.ufms.facom.sp.tools;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public final class AES implements Serializable {
    
    private final byte[] chave;
    
    private static final String ALGORITMO = "AES";
    
    private static final String TRANSFORMACAO = "AES/ECB/PKCS5Padding";
        
    private static final AESTamanhoChave TAM_PADRAO_CHAVE = AESTamanhoChave.de128Bits;
        
    public enum AESTamanhoChave {
        de128Bits, de192Bits, de256Bits;
        public int getQtdeBits(){
            switch(this){
                case de128Bits : return 128;
                case de192Bits : return 192;
                case de256Bits : return 256;
                default: return 128;
            }
        }
        public static AESTamanhoChave getTamanhoDaChave(int tamanhoChave){
            switch(tamanhoChave){
                case 128 : return de128Bits;
                case 192 : return de192Bits;
                case 256 : return de256Bits;
                default: return null;
            }            
        }
    }

    public AES() throws NoSuchAlgorithmException {
        this.chave = gerarChaveAleatoria(TAM_PADRAO_CHAVE);
    }    
    
    public AES(byte[] chave) {
        this.chave = chave;
    }
    
    public AES(File arquivo) throws FileNotFoundException, IOException {
        this.chave = carregarChaveDoArquivo(arquivo);
    }
    
    public byte[] criptografar( byte[] entrada, byte[] chave ) throws NoSuchAlgorithmException, 
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, 
            BadPaddingException, InvalidParameterSpecException {
        if(chave==null){
            return null;
        }        
                
        SecretKeySpec chaveSpec = new SecretKeySpec(chave,ALGORITMO);
        Cipher cipher = Cipher.getInstance(TRANSFORMACAO);
        cipher.init(Cipher.ENCRYPT_MODE, chaveSpec);
        return cipher.doFinal(entrada);       
    }    

    public void criptografar( byte[] chave, File entrada, File saida ) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, InvalidParameterSpecException, IOException {
        if(entrada==null||saida==null){
            return;
        }
        
        FileInputStream  is = new FileInputStream(entrada);
        FileOutputStream os = new FileOutputStream(saida);

        SecretKeySpec chaveSpec = new SecretKeySpec(chave,ALGORITMO);
        Cipher cipher = Cipher.getInstance(TRANSFORMACAO);
        cipher.init(Cipher.ENCRYPT_MODE, chaveSpec);

        byte[] buffer = new byte[1024];
        int contador = is.read(buffer);
        while (contador >= 0) {
            os.write(cipher.update(buffer, 0, contador));
            contador = is.read(buffer);
        }
        os.write(cipher.doFinal());
        os.flush();
        os.close();
    }    

    public void criptografar( File entrada, File saida ) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, InvalidParameterSpecException, IOException {
        criptografar(chave, entrada, saida);
    }    
    
    public byte[] criptografar( byte[] entrada ) throws NoSuchAlgorithmException, 
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, 
            BadPaddingException, InvalidParameterSpecException {
        if(chave==null){
            return null;
        }        
        return criptografar(entrada,chave);
    }
    
    public byte[] descriptografar( byte[] entrada, byte[] chave ) throws NoSuchAlgorithmException, 
            NoSuchPaddingException, InvalidKeyException, 
            IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, 
            InvalidAlgorithmParameterException {
        if(chave==null){
            return null;
        }        
        SecretKeySpec chaveSpec = new SecretKeySpec(chave,ALGORITMO);        
        Cipher cipher = Cipher.getInstance(TRANSFORMACAO);
        cipher.init(Cipher.DECRYPT_MODE, chaveSpec);
        return cipher.doFinal(entrada);
    }
    
    public void descriptografar( byte[] chave, File entrada, File saida ) 
            throws NoSuchAlgorithmException, 
            NoSuchPaddingException, InvalidKeyException, 
            IllegalBlockSizeException, FileNotFoundException, IOException, 
            BadPaddingException, NoSuchProviderException, 
            InvalidAlgorithmParameterException {
        
        FileInputStream is = new FileInputStream(entrada);
        FileOutputStream os = new FileOutputStream(saida);

        SecretKeySpec chaveSpec = new SecretKeySpec(chave,ALGORITMO);        
        Cipher cipher = Cipher.getInstance(ALGORITMO);
        cipher.init(Cipher.DECRYPT_MODE, chaveSpec);

        byte[] buffer = new byte[1024];
        int contador = is.read(buffer);
        while (contador >= 0) {
            os.write(cipher.update(buffer, 0, contador)); 
            contador = is.read(buffer);
        }
        os.write(cipher.doFinal());
        os.flush();
        os.close();
    }  
    
    public byte[] descriptografar( byte[] chave, File entrada ) 
            throws NoSuchAlgorithmException, 
            NoSuchPaddingException, InvalidKeyException, 
            IllegalBlockSizeException, FileNotFoundException, IOException, 
            BadPaddingException, NoSuchProviderException, 
            InvalidAlgorithmParameterException {
        
        FileInputStream is = new FileInputStream(entrada);
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        SecretKeySpec chaveSpec = new SecretKeySpec(chave,ALGORITMO);        
        Cipher cipher = Cipher.getInstance(ALGORITMO);
        cipher.init(Cipher.DECRYPT_MODE, chaveSpec);

        byte[] buffer = new byte[1024];
        int contador = is.read(buffer);
        while (contador >= 0) {
            os.write(cipher.update(buffer, 0, contador)); 
            contador = is.read(buffer);
        }
        os.write(cipher.doFinal());
        os.flush();
        os.close();
        return os.toByteArray();
    }     
    
    public byte[] descriptografar( File entrada )throws NoSuchAlgorithmException, 
            NoSuchPaddingException, InvalidKeyException, 
            IllegalBlockSizeException, FileNotFoundException, IOException, 
            BadPaddingException, NoSuchProviderException, 
            InvalidAlgorithmParameterException {
        return descriptografar(chave, entrada);
    }
   
    public void descriptografar( File entrada, File saida )  
            throws NoSuchAlgorithmException, 
            NoSuchPaddingException, InvalidKeyException, 
            IllegalBlockSizeException, FileNotFoundException, IOException, 
            BadPaddingException, NoSuchProviderException, 
            InvalidAlgorithmParameterException {
        descriptografar(chave, entrada, saida);
    }
    
    public byte[] descriptografar( byte[] entrada ) throws NoSuchAlgorithmException, 
            NoSuchPaddingException, InvalidKeyException, 
            IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, 
            InvalidAlgorithmParameterException {
        if(chave==null){
            return null;
        }
        return descriptografar(entrada,chave);
    }
    
    public static byte[] gerarChaveAleatoria( AESTamanhoChave tamChave )
            throws NoSuchAlgorithmException {
        KeyGenerator gerador = KeyGenerator.getInstance(ALGORITMO);
        gerador.init(tamChave.getQtdeBits());
        SecretKey chaveSecreta = gerador.generateKey();
        return chaveSecreta.getEncoded();
    }
        
    public static void gravarChaveEmArquivo(File arquivo, 
            byte[] chave) throws IOException{
        if(arquivo==null||chave==null){
            return;
        }
        FileOutputStream os = new FileOutputStream(arquivo);
        os.write(chave);
        os.flush();
        os.close();
    }

    public void gravarChaveEmArquivo(File arquivo) throws IOException{
        gravarChaveEmArquivo(arquivo, chave);
    }
    
    public byte[] carregarChaveDoArquivo(File arquivo) 
            throws FileNotFoundException, IOException {
        if(arquivo==null){
            return null;
        }
        BufferedReader br = new BufferedReader(new FileReader(arquivo));        
        String linha = "";
        while (br.ready()) {
            linha += br.readLine();
        }
        br.close();
        return linha.getBytes();
    }    
    
    public static void main(String args[]) throws Exception {
        try {
            
            System.out.println("***********************************************");
            System.out.println("Esse construtor gera uma chave aleatória automaticamente.");
            AES aes = new AES();                   
            aes.gravarChaveEmArquivo(new File("chave1.txt"));
            byte[] criptografado = aes.criptografar("teste".getBytes());
            System.out.println("Criptografado: "+new String(criptografado));
            byte[] descriptografado = aes.descriptografar(criptografado);
            System.out.println("Descriptografado: "+new String(descriptografado));            
            
            
            System.out.println("***********************************************");
            System.out.println("Esse construtor carrega a chave de um arquivo.");
            aes=new AES(new File("chave1.txt"));
            criptografado = aes.criptografar("teste1".getBytes());
            System.out.println("Criptografado: "+new String(criptografado));
            descriptografado = aes.descriptografar(criptografado);
            System.out.println("Descriptografado: "+new String(descriptografado));         
            
                        
            System.out.println("***********************************************");
            aes=new AES(new File("chave1.txt"));
            
            FileWriter fw = new FileWriter(new File("entrada.txt"));
            fw.write("teste2asdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasd\n");
            fw.write("teste2asdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasd\n");
            fw.write("teste2asdfasdfasdfasdfasdfasdfasdfa\n");
            fw.write("teste2asdfasdfasdfasdfasdfasdfasddddddddddddfa\n");
            fw.write("teste2asdfasdfasdfasdfasdfasdfasdddddddddddxccccccdfa\n");
            fw.write("ABCDEFGHIJKLMNOPQRSTUVXZ\n");
            fw.close();
            
            aes.criptografar(new File("entrada.txt"),new File("saida.txt"));
            
            BufferedReader br = new BufferedReader(new FileReader(new File("saida.txt")));
            String linha = "";
            while (br.ready()) {
                linha += br.readLine();
            }
            br.close();
            
            System.out.println("Criptografado: " + linha);
            
            aes.descriptografar(new File("saida.txt"),new File("saida-descritografada.txt"));
            
            br = new BufferedReader(new FileReader(new File("saida-descritografada.txt")));
            linha = "";
            while (br.ready()) {
                linha += br.readLine();
            }
            br.close();
            
            System.out.println("Descriptografado: " + linha);
                                    
            System.out.println("***********************************************");            
            
        } catch (Exception e){
            System.out.println("Erro!");   
            e.printStackTrace();
        }
        
    }
    
}