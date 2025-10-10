package com.dsp.plot.onlyoffice.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.dsp.plot.dto.SerialNumberRequest;
import com.dsp.plot.dto.SerialNumberResponse;
import com.dsp.plot.entity.PlotHistory;
import com.dsp.plot.entity.PlotInput;
import com.dsp.plot.onlyoffice.DocumentConfigDTO;
import com.dsp.plot.onlyoffice.DocumentRequestDTO;
import com.dsp.plot.onlyoffice.OnlyOfficeMetadata;
import com.dsp.plot.onlyoffice.OnlyOfficeMetadataRepository;
import com.dsp.plot.repository.MasterLSSRepository;
import com.dsp.plot.repository.MastersRSSRepository;
import com.dsp.plot.repository.MyUserRepository;
import com.dsp.plot.repository.PlotGenerateRepository;
import com.dsp.plot.repository.PlotHistoryRepository;
import com.dsp.plot.repository.PlotIndexRepository;
import com.dsp.plot.repository.PlotInputRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class OnlyOfficeService {

    @Value("${workflow.service.url}")
    private String draftUrl;
    @Value("${feign.client.bearer.url}")
    private String serviceUrl;
    @Value("${download.WL.url}")
    private String downloadKmUrl;
    @Value("${upload.WL.url}")
    private String uploadOkmUrl;
    private static final String LS = "LS";
    private static final String RS = "RS";

    @Autowired
    private OnlyOfficeMetadataRepository onlyOfficeMetadataRepository;
    private static final Logger log = LoggerFactory.getLogger(OnlyOfficeService.class);
    @Autowired
    private HttpServletRequest httpRequest;

    @Autowired
    private PlotInputRepository inputRepository;
    @Autowired
    private PlotHistoryRepository historyRepository;
    @Autowired
    private RestTemplate restTemplate;

    public String getTokenFromRequest() {
        String authorizationHeader = httpRequest.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    public DocumentConfigDTO getOnlyOfficeConfig(DocumentRequestDTO requestDTO) {
        String url = serviceUrl + "/only-office/config";
        // String url = "http://localhost:8222/only-office/config";
        String token = getTokenFromRequest();
        if (token == null) {
            throw new IllegalStateException("Authorization token is missing");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<DocumentRequestDTO> entity = new HttpEntity<>(requestDTO, headers);

        // System.out.println("url "+url);
        // System.out.println("entity "+entity);
        try {
            return restTemplate.postForObject(url, entity, DocumentConfigDTO.class);
        } catch (Exception e) {
            System.err.println("Error during API call: " + e.getMessage());
            throw e;
        }

    }

    public static String fixUnclosedTags(String html) {
        // Simple fix for <br> and <hr>
        html = html.replaceAll("<br>", "<br/>");
        html = html.replaceAll("<hr>", "<hr/>");
        // Remove <em> wrapping block elements
        html = html.replaceAll("<em>\\s*<div", "<div");
        html = html.replaceAll("</div>\\s*</em>", "</div>");
        if (!html.contains("</body>")) {
            html += "</body>";
        }
        if (!html.contains("</html>")) {
            html += "</html>";
        }

        return html;
    }

    public String fixComment(String content) {
        String contentFix = content
                .replaceAll("<br>", "<br/>") // self-close <br>
                .replaceAll("&nbsp;", " ") // remove non-breaking space
                .replaceAll("<!--.*?-->", ""); // remove comments
        contentFix = removeExtraClosingTags(contentFix);
        contentFix = fixUnclosedTags(contentFix);

        // // --- Add Jsoup parsing to ensure proper XML syntax ---
        // Document doc = Jsoup.parse(contentFix);
        // doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml); // closes tags
        // contentFix = doc.html();

        return contentFix;
    }

    public File byteArrayResourceToFile(ByteArrayResource resource, String prefix, String suffix) throws Exception {
        // Create a temp file
        File tempFile = File.createTempFile(prefix, suffix);

        // Write the content of the ByteArrayResource to the file
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(resource.getByteArray());
        }

        return tempFile;
    }

    public String uploadPdfToOpenKM(File pdfFile, String documentName, String folderPath, String masterId,
            String referenceId, String referenceType, String applicationNo) {
        try {
            System.out.println(">>> calling uploadPdfToOpenKM -");
            System.out.println(">>> documentName - " + documentName);
            System.out.println(">>> folderPath - " + folderPath);
            System.out.println(">>> masterId - " + masterId);
            System.out.println(">>> referenceId - " + referenceId);
            System.out.println(">>> referenceType - " + referenceType);
            System.out.println(">>> applicationNo - " + applicationNo);

            // The JWT token is automatically added by the RestTemplate interceptor
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Create a FileSystemResource from the file
            FileSystemResource fileResource = new FileSystemResource(pdfFile);

            // Prepare the body with all required fields
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("folderPath", folderPath);
            body.add("documentName", documentName);
            body.add("file", fileResource);
            body.add("masterId", masterId);
            body.add("referenceId", referenceId);
            body.add("referenceType", referenceType);
            body.add("applicationNo", applicationNo);

            System.out.println(">>> inside");

            // Prepare the request entity with headers and body
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            System.out.println(requestEntity);
            // String uploadUrl =
            // "https://dspstg.sansad.in/dms/openkm/dsp1/document/upload";
            System.out.println(">>> NEW uploadUrl - " + uploadOkmUrl);
            ResponseEntity<String> response = restTemplate.exchange(uploadOkmUrl, HttpMethod.POST, requestEntity,
                    String.class);

            // Return the response body as a string
            return response.getBody();
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            throw new RuntimeException("Error uploading document to OpenKM. HTTP Error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error uploading document to OpenKM.", e);
        }
    }

    public void convertAndUpdateMetadata(String content, String dsp2Key, String downloadUrl, String fileName,
            String applicationId) {
        content = fixComment(content);
        try {
            ByteArrayResource docxFile = convertHtmlToDocxDirect(content);
            // convertHtmlToDocxDirect
            File tempFile = byteArrayResourceToFile(docxFile, "document-" + dsp2Key, ".docx");
            String openKmResponse = uploadPdfToOpenKM(tempFile, fileName, "/okm:root/only-office/", "231",
                    "51303", "ApplicationInstance", "1");
            JsonNode rootNode = new ObjectMapper().readTree(openKmResponse);
            String documentUuid = rootNode.path("data").path("documentUuid").asText();
            // save in Only officemetadata
            String fileUrl = downloadUrl;
            String downloadOKMUrl = downloadKmUrl + documentUuid;
            // 5. Update or insert into OnlyOfficeMetadata
            Optional<OnlyOfficeMetadata> existingMetadataOpt = onlyOfficeMetadataRepository.findByDsp2Key(dsp2Key);
            OnlyOfficeMetadata metadata;
            if (existingMetadataOpt.isPresent()) {
                metadata = existingMetadataOpt.get();
                metadata.setDownloadUrl(downloadOKMUrl);
                metadata.setOnlyOfficeUrl(fileUrl);
                metadata.setUpdatedBy(1L);
                metadata.setUpdatedAt(LocalDateTime.now());
                metadata.setDocumentKey(documentUuid);
                metadata.setApplicationId(Long.parseLong(applicationId));
            } else {
                metadata = OnlyOfficeMetadata.builder().applicationId(Long.parseLong(applicationId))
                        .documentKey(documentUuid).dsp2Key(dsp2Key).downloadUrl(downloadOKMUrl)
                        .onlyOfficeUrl(fileUrl)
                        .createdBy(1L).updatedBy(1L).build();
            }
            metadata = onlyOfficeMetadataRepository.save(metadata);
            System.out.println("meta data");
            System.out.println(metadata);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove unmatched closing tags like </div>, </em>,
     * </p>
     * Only removes if there is no matching opening tag.
     */
    public String removeExtraClosingTags(String html) {
        if (html == null)
            return null;

        // Regex to match all opening and closing tags
        Pattern tagPattern = Pattern.compile("<(/?)([a-zA-Z0-9]+)([^>]*)>");
        Matcher matcher = tagPattern.matcher(html);

        Stack<String> stack = new Stack<>();
        StringBuffer cleanedHtml = new StringBuffer();

        int lastEnd = 0;
        while (matcher.find()) {
            String fullTag = matcher.group(0);
            String closingSlash = matcher.group(1); // "/" if closing
            String tagName = matcher.group(2);

            // Append text before tag
            cleanedHtml.append(html, lastEnd, matcher.start());

            if (closingSlash.isEmpty()) {
                // Opening tag → push to stack
                stack.push(tagName.toLowerCase());
                cleanedHtml.append(fullTag);
            } else {
                // Closing tag → check stack
                if (!stack.isEmpty() && stack.peek().equals(tagName.toLowerCase())) {
                    stack.pop();
                    cleanedHtml.append(fullTag);
                } else {
                    // Skip unmatched closing tag
                    // System.out.println("Removed unmatched closing tag: " + fullTag);
                }
            }

            lastEnd = matcher.end();
        }

        // Append remaining text
        cleanedHtml.append(html.substring(lastEnd));

        return cleanedHtml.toString();
    }

    public static String cleanHtmlForDocx(String htmlContent) {
    // 1. Parse and clean with Jsoup
    Document doc = Jsoup.parse(htmlContent);
    doc.outputSettings()
        .syntax(Document.OutputSettings.Syntax.xml) // close tags
        .escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml)
        .prettyPrint(false);

    // 2. Remove illegal nested tags manually
    doc.select("p p").unwrap();   // unwrap nested <p>
    doc.select("h2 p").unwrap();  // unwrap <p> inside <h2>
    doc.select("span p").unwrap();// unwrap <p> inside <span>

    // 3. Ensure body/html end tags exist
    if (!doc.outerHtml().endsWith("</body></html>")) {
        String html = doc.outerHtml();
        if (!html.contains("</body>")) html += "</body>";
        if (!html.contains("</html>")) html += "</html>";
        return html;
    }

    return doc.outerHtml();
}

    public ByteArrayResource convertHtmlToDocxDirect(String htmlContent) throws Exception {

        System.out.println("html " + htmlContent);
        // 1. Clean the HTML using Jsoup
        Document doc = Jsoup.parse(htmlContent);
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml); // closes tags
        doc.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml); // escape entities like &nbsp;
        String cleanedHtml = doc.html();

        System.out.println("Cleaned HTML: " + cleanedHtml);
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);
        // Convert HTML to WordML elements
        List<Object> htmlObjects = xhtmlImporter.convert(cleanedHtml, null);
        // List<Object> htmlObjects = xhtmlImporter.convert(htmlContent, null);

        // Add the converted elements into the main document
        wordMLPackage.getMainDocumentPart().getContent().addAll(htmlObjects);
        // Save to memory
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wordMLPackage.save(baos);
        return new ByteArrayResource(baos.toByteArray());
        // return new ByteArrayResource(null);
    }

    public Map<String, Object> getOnlyOfficeInput(String refId) {
        // TODO Auto-generated method stub
        Map<String, Object> res = new HashMap<>();
        Long id = Long.parseLong(refId);
        Optional<PlotInput> input = inputRepository.findById(id);
        if (input.isEmpty()) {
            throw new RuntimeException("No data found with shared ref id " + refId);
        }

        PlotInput inputContent = input.get();
        res.put("input-content", inputContent.getContent());
        return res;

    }

    public Map<String, Object> getOnlyOfficeInputForGeneration(String refId) {
        // TODO Auto-generated method stub
        Map<String, Object> res = new HashMap<>();
        Long id = Long.parseLong(refId);
        Optional<PlotHistory> history = historyRepository.findPlotHistory(id);
        if (history.isEmpty()) {
            throw new RuntimeException("No data found with shared ref id " + refId);
        }

        PlotHistory historyInput = history.get();
        res.put("input-content", historyInput.getAddedText());
        return res;
    }

    public void uloadAndUpdateMetadata(String content, String dsp2Key, String downloadUrl, String fileName,
            String applicationId) {
        // TODO Auto-generated method stub
        convertAndUpdateMetadata(content, dsp2Key, downloadUrl, fileName, applicationId);

    }

}