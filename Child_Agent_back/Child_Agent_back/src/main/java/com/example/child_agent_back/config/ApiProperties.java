package com.example.child_agent_back.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * API配置属性类
 * 使用@ConfigurationProperties注解自动绑定配置文件中的API相关配置
 */
@Component
@ConfigurationProperties(prefix = "av.api")
public class ApiProperties {

    // 语音识别API配置
    private VoiceApi voice = new VoiceApi();
    
    // 大语言模型API配置
    private ChatApi chat = new ChatApi();
    
    // 图片分析API配置
    private ImageApi image = new ImageApi();

    public VoiceApi getVoice() {
        return voice;
    }

    public void setVoice(VoiceApi voice) {
        this.voice = voice;
    }

    public ChatApi getChat() {
        return chat;
    }

    public void setChat(ChatApi chat) {
        this.chat = chat;
    }

    public ImageApi getImage() {
        return image;
    }

    public void setImage(ImageApi image) {
        this.image = image;
    }

    /**
     * 语音识别API配置内部类
     */
    public static class VoiceApi {
        private RecognizeApi recognize = new RecognizeApi();

        public RecognizeApi getRecognize() {
            return recognize;
        }

        public void setRecognize(RecognizeApi recognize) {
            this.recognize = recognize;
        }

        public static class RecognizeApi {
            private String apiKey;
            private String appId;
            private String secretKey;
            private String endpoint;

            public String getApiKey() {
                return apiKey;
            }

            public void setApiKey(String apiKey) {
                this.apiKey = apiKey;
            }

            public String getAppId() {
                return appId;
            }

            public void setAppId(String appId) {
                this.appId = appId;
            }

            public String getSecretKey() {
                return secretKey;
            }

            public void setSecretKey(String secretKey) {
                this.secretKey = secretKey;
            }

            public String getEndpoint() {
                return endpoint;
            }

            public void setEndpoint(String endpoint) {
                this.endpoint = endpoint;
            }

            /**
             * 检查配置是否完整
             */
            public boolean isValid() {
                return apiKey != null && !apiKey.isEmpty() && 
                       appId != null && !appId.isEmpty() && 
                       endpoint != null && !endpoint.isEmpty();
            }
        }
    }

    /**
     * 大语言模型API配置内部类
     */
    public static class ChatApi {
        private String apiKey;
        private String model;
        private String endpoint;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        /**
         * 检查配置是否完整
         */
        public boolean isValid() {
            return apiKey != null && !apiKey.isEmpty() && 
                   endpoint != null && !endpoint.isEmpty();
        }
    }

    /**
     * 图片分析API配置内部类
     */
    public static class ImageApi {
        private AnalyzeApi analyze = new AnalyzeApi();

        public AnalyzeApi getAnalyze() {
            return analyze;
        }

        public void setAnalyze(AnalyzeApi analyze) {
            this.analyze = analyze;
        }

        public static class AnalyzeApi {
            private String apiKey;
            private String endpoint;

            public String getApiKey() {
                return apiKey;
            }

            public void setApiKey(String apiKey) {
                this.apiKey = apiKey;
            }

            public String getEndpoint() {
                return endpoint;
            }

            public void setEndpoint(String endpoint) {
                this.endpoint = endpoint;
            }

            /**
             * 检查配置是否完整
             */
            public boolean isValid() {
                return apiKey != null && !apiKey.isEmpty() && 
                       endpoint != null && !endpoint.isEmpty();
            }
        }
    }
}