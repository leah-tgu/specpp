package org.processmining.specpp.prom.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class EnumTransferable<E extends Enum<E>> implements Transferable {

    private final E data;
    private DataFlavor myFlavor;

    public EnumTransferable(E data) {
        this.data = data;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        myFlavor = new DataFlavor(Enum.class, "false");
        return new DataFlavor[]{myFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(myFlavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return data;
    }

}
