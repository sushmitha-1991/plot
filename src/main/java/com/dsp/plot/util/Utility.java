package com.dsp.plot.util;

import com.dsp.plot.dto.HouseDTO;
import com.dsp.plot.dto.UserDetailsDTO;
import java.io.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.thymeleaf.context.Context;


/**
 *
 * @author
 */
@Component
public class Utility {
    
    @Autowired
    private TemplateEngine templateEngine;
        
    public static HouseDTO getHouse() {
        
	HouseDTO hdto = new HouseDTO(); 
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsDTO) {
            long houseId = ((UserDetailsDTO) principal).getHouseId();
            //String abbr = ((UserDetailsDTO) principal).getHouseAbbr();
            hdto.setId(houseId);
            //h.setAbbr(abbr);
        }
        else {
            throw new RuntimeException("Bad Request");
        }
        return hdto;
    }
    
    public byte[] generatePdf(String templateName, Context context) throws Exception {
        String html = templateEngine.process(templateName, context);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream, true);
        byte[] pdfBytes = outputStream.toByteArray();
        outputStream.close();
        return pdfBytes;
    }    
}
