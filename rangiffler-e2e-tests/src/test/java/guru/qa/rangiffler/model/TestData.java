package guru.qa.rangiffler.model;

import guru.qa.rangiffler.data.projection.PhotoWithLikes;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public record TestData(
        String password,
        List<PhotoWithLikes> photos,
        List<UdUserJson> friends,
        List<UdUserJson> incomeInvitations,
        List<UdUserJson> outcomeInvitations
) {
    public TestData(String password) {
        this(password, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}