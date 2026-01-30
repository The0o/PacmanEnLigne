package designPattern;

public interface Observable {
    void enregistrerObservateur(Observateur observateur);

    void supprimerObservateur(Observateur observateur);

    void notifierObservateurs();
}
