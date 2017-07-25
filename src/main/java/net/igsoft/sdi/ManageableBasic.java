package net.igsoft.sdi;

import java.io.Closeable;

public interface ManageableBasic extends Closeable {

    void init();

    @Override
    void close();
}
