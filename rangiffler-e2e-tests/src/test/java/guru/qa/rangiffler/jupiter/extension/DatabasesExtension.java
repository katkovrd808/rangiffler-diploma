package guru.qa.rangiffler.jupiter.extension;

import guru.qa.rangiffler.data.jdbc.Connections;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DatabasesExtension implements SuiteExtension {

    public void afterSuite() {
        Connections.closeAllConnections();
    }
}
