package designPattern;

import vue.ViewCommand;

public class EtatLance implements EtatJeu {

    @Override
    public void updateBouton(ViewCommand view) {
        view.distribuerBoutons(true, true, false, false);
    }
    
}
