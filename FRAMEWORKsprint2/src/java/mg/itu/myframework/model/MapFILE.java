package mg.itu.myframework.model;

public class MapFILE {
    private String nom_du_fichier;
    private String type_fichier;
    private byte[] bytes;

    public MapFILE() {
    }

    public MapFILE(String nom_du_fichier, String type_fichier, byte[] bytes) {
        this.nom_du_fichier = nom_du_fichier;
        this.type_fichier = type_fichier;
        this.bytes = bytes;
    }

    public String getNom_du_fichier() {
        return nom_du_fichier;
    }

    public void setNom_du_fichier(String nom_du_fichier) {
        this.nom_du_fichier = nom_du_fichier;
    }

    public String getType_fichier() {
        return type_fichier;
    }

    public void setType_fichier(String type_fichier) {
        this.type_fichier = type_fichier;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}