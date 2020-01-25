package ch.so.agi.grundstuecksinformation.shared;

import java.io.IOException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("egrid")
public interface EgridService extends RemoteService {
    EgridResponse egridServer(String XY) throws IllegalArgumentException, IOException;
}
