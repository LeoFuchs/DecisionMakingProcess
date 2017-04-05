package br.ufms.facom.sp.controller;

import br.ufms.facom.sp.model.Constant;
import br.ufms.facom.sp.model.User;
import br.ufms.facom.sp.tools.AES;
import br.ufms.facom.sp.tools.FileHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO implements Serializable {

    /*SINGLETON GoF*/
    private static UserDAO INSTANCE;
            
    private UserDAO() {
        
    }
    
    public boolean login(String user, String password) throws Exception {
        List<User> userList = getUserList();
        if(!userList.isEmpty()){
            for(User u : userList){
                if(u.getUser().equals(user)&&
                        u.getPassword().equals(password)){
                    return true;
                }
            }
        }
        return false;
    }
    
    public List<User> getUserList() throws Exception {
        AES aes = new AES(Constant.KEY);
        
        File ujFile = new File("users.json");
        String userJson = new String(aes.descriptografar(ujFile));
        
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<User>>(){}.getType();
        List<User> userList = gson.fromJson(userJson,listType);
        
        return userList;
    }
    
    public void saveUserList(List<User> userList) throws Exception{
        Gson gson = new Gson();
        String userJson = gson.toJson(userList);
        AES aes = new AES(Constant.KEY);
        byte[] userJsonCrypto = aes.criptografar(userJson.getBytes());
        FileHelper.saveFile(userJsonCrypto, new File("users.json"));
    }
    
    public static UserDAO getINSTANCE(){
        if(INSTANCE==null){
            INSTANCE = new UserDAO();
        }
        return INSTANCE;
    }
    
    public static void main(String args[]) throws Exception{

        List<User> userList;
        try {
            UserDAO userDAO = getINSTANCE();
            userList = userDAO.getUserList();
            for(User user : userList){
                System.out.println("User: " + user.getFullName());
            }
            
            /*User user = new User();
            user.setFullName("Fernando");
            user.setUser("flamas");
            user.setPassword("teste");
            userList.add(user);*/
            
            userDAO.saveUserList(userList); 
        } catch (IOException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
  
        /*
        try {
            AES aes = new AES(Constant.KEY);
            aes.criptografar(new File("users.json"), new File("users.cripto.json"));
            System.out.println("OK!");
        } catch (InvalidKeyException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidParameterSpecException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        

    }
    
}