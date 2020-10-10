/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPTemplate;
import database.model.DBUser;
import model.FPUser;

/**
 *
 * @author Kenshi
 */
public class UserUtils {
    
    public static FPUser convertDBUserToFPUser(DBUser user){
        DPFPTemplate template = DPFPGlobal.getTemplateFactory()
                .createTemplate(user.getFingerPrint());      
        return new FPUser(template,user.getId());
    }
    
}
