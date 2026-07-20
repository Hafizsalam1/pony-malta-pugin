package resolver;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import util.LayerType;

public class LayerResolver {

    public LayerType resolveMethodLayer(PsiMethod method) {

        if (method == null) {
            return LayerType.UNKNOWN;
        }

        PsiClass psiClass = method.getContainingClass();

        if (psiClass == null) {
            return LayerType.UNKNOWN;
        }


        return resolveClassLayer(psiClass);
    }



    /**
     * Resolve layer berdasarkan class
     */
    public LayerType resolveClassLayer(PsiClass psiClass) {

        if (psiClass == null) {
            return LayerType.UNKNOWN;
        }


        // =========================
        // CONTROLLER
        // =========================
        if (hasAnnotation(
                psiClass,
                "org.springframework.web.bind.annotation.RestController")
                ||
                hasAnnotation(
                        psiClass,
                        "org.springframework.stereotype.Controller")) {

            return LayerType.CONTROLLER;
        }



        // =========================
        // SERVICE
        // =========================
        if (hasAnnotation(
                psiClass,
                "org.springframework.stereotype.Service")) {

            return LayerType.SERVICE;
        }



        // =========================
        // REPOSITORY
        // =========================
        if (hasAnnotation(
                psiClass,
                "org.springframework.stereotype.Repository")
                ||
                isRepositoryInterface(psiClass)) {

            return LayerType.REPOSITORY;
        }



        // =========================
        // ENTITY
        // =========================
        if (hasAnnotation(
                psiClass,
                "jakarta.persistence.Entity")
                ||
                hasAnnotation(
                        psiClass,
                        "javax.persistence.Entity")) {

            return LayerType.ENTITY;
        }



        String className = psiClass.getName();


        if (className != null) {


            // =========================
            // DTO
            // =========================
            if (isDTO(className)) {
                return LayerType.DTO;
            }



            // =========================
            // EXTERNAL CLIENT
            // =========================
            if (isExternal(className)) {
                return LayerType.EXTERNAL;
            }



            // =========================
            // UTILITY
            // =========================
            if (isUtility(className)) {
                return LayerType.UTILITY;
            }
        }



        // =========================
        // CONFIGURATION
        // =========================
        if (hasAnnotation(
                psiClass,
                "org.springframework.context.annotation.Configuration")) {

            return LayerType.CONFIGURATION;
        }



        return LayerType.UNKNOWN;
    }





    private boolean hasAnnotation(
            PsiClass psiClass,
            String annotationName) {

        return psiClass.hasAnnotation(annotationName);
    }




    private boolean isRepositoryInterface(PsiClass psiClass) {

        if (!psiClass.isInterface()) {
            return false;
        }


        String name = psiClass.getName();

        return name != null
                && name.endsWith("Repository");
    }





    private boolean isDTO(String className) {

        return className.endsWith("DTO")
                || className.endsWith("Request")
                || className.endsWith("Response")
                || className.endsWith("Payload");
    }





    private boolean isExternal(String className) {

        return className.endsWith("Client")
                || className.endsWith("Adapter")
                || className.endsWith("Feign")
                || className.endsWith("Connector");
    }





    private boolean isUtility(String className) {

        return className.endsWith("Util")
                || className.endsWith("Utils")
                || className.endsWith("Helper");
    }

}
