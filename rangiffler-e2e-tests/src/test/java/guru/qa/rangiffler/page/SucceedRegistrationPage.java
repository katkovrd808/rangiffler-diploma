package guru.qa.rangiffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class SucceedRegistrationPage {
    private final SelenideElement
        congratulationText = $(".form__paragraph_success"),
        loginBtn = $(".form_sign-in");

    @Nonnull
    @Step("Asserting congrats to be equal: Congratulations! You've registered!")
    public SucceedRegistrationPage checkSucceedRegistrationPageTitle(){
        congratulationText.shouldHave(text("Congratulations! You've registered!"));
        return this;
    }

    @Nonnull
    @Step("Logging in after registration")
    public WelcomePage loginAfterRegistration(){
        loginBtn.click();
        return new WelcomePage();
    }
}
