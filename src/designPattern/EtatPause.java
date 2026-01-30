package designPattern;

import vue.ViewCommand;

public class EtatPause implements EtatJeu {

    @Override
    public void updateBouton(ViewCommand view) {
        view.distribuerBoutons(true, false, true, true);
    }
    
}
