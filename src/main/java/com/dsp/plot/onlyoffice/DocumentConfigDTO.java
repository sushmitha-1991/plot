package com.dsp.plot.onlyoffice;

import lombok.Data;
import java.util.Map;

@Data
public class DocumentConfigDTO {

    private String dsp2Key;
    private EditorConfig editorConfig;
    private String documentType;
    private String documentKey;
    private Document document;

    @Data
    public static class EditorConfig {
        private String mode;
        private CoEditing coEditing;
        private Customization customization;
        private String callbackUrl;

        @Data
        public static class CoEditing {
            private String mode;
            private boolean change;
        }

        @Data
        public static class Customization {
            private boolean save;
            private boolean forcesave;
        }
    }

    @Data
    public static class Document {
        private Permissions permissions;
        private String title;
        private String key;
        private String url;

        @Data
        public static class Permissions {
            private boolean edit;
        }
    }
}
