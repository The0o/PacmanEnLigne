package designPattern;

import vue.ViewCommand;

public class EtatStoppe implements EtatJeu {

    @Override
    public void updateBouton(ViewCommand view) {
        view.distribuerBoutons(false, false, true, true);
    }
    
}
