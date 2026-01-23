package guru.qa.rangiffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.config.Config;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class RegistrationPage {
    private final SelenideElement
        usernameInput = $("#username"),
        passwordInput = $("#password"),
        passwordSubmitInput = $("#passwordSubmit"),
        registrationForm = $("#register-form"),
        signupButton = $("button[type='submit']"),
        formError = $(".form__error"),
        loginButton = $("#register-form a");

    private final static Config CFG = Config.getInstance();

    public final static String URL = CFG.authUrl() + "register";

    @Nonnull
    @Step("Entering user {username} data to registration form")
    public RegistrationPage fillRegistrationForm(String username, String password, String submittedPassword){
        usernameInput.val(username);
        passwordInput.val(password);
        passwordSubmitInput.val(submittedPassword);
        return this;
    }

    @Nonnull
    @Step("Submitting registration form")
    public SucceedRegistrationPage submitRegistration(){
        signupButton.click();
        return new SucceedRegistrationPage();
    }

    @Nonnull
    @Step("Asserting username field error text to be equal {error}")
    public RegistrationPage submitRegistrationAndCheckUsernameError(String error){
        signupButton.click();
        registrationForm.$$("label").find(text("Username"))
                .$("span").shouldHave(text(error));
        return this;
    }

    @Nonnull
    @Step("Asserting form error text be to equal {error}")
    public RegistrationPage completeRegistrationAndCheckFormError(String error) {
        signupButton.click();
        formError.shouldHave(text(error));
        return this;
    }

    @Nonnull
    @Step("Asserting password field error text to be equal {error}")
    public RegistrationPage submitRegistrationAndCheckPasswordError(String error){
        signupButton.click();
        registrationForm.$$("label").find(text("Password"))
                .$("span").shouldHave(text(error));
        return this;
    }
}
