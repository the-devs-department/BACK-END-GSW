        package com.gsw.taskmanager.entity;

        import java.io.Serializable;

        import org.springframework.data.annotation.Id;
        import org.springframework.data.mongodb.core.mapping.Document;

        import jakarta.validation.constraints.NotNull;
        import lombok.Data;
        import lombok.Getter;

        @Data
        @Getter
        @Document(collection = "equipes")
        public class Equipe implements 
        Serializable {
            
            private static final long serialVersionUID = 1L;

            @Id
            private String id;

            @NotNull
            private String titulo;

            @NotNull
            private String nome;

            @NotNull
            private String userEmail;

            @NotNull
            private String adminEmail;
        }
