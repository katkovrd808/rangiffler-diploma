export const MAX_PHOTO_DESCRIPTION_LENGTH = 50;
export const MAX_PHOTO_DESCRIPTION_ERROR = "Description length has to be not longer that 50 symbols";
export const EMPTY_SRC_ERROR = "Please upload an image";
export const EMPTY_COUNTRY_ERROR = "You have to select country";

export interface IStringIndex extends Record<string, any> {
};

export type PhotoFormProps = {
    [key: string]: {
        value: string | undefined;
        error: boolean;
        errorMessage: string;
    } | string | undefined;

    description: {
        value: string;
        error: boolean;
        errorMessage: string;
    };
    country: {
        value: string;
        error: boolean;
        errorMessage: string;
    };
    src: {
        value: string;
        error: boolean;
        errorMessage: string;
    };
    id?: string;
};

export const isFormField = (key: string, _obj: PhotoFormProps): key is 'description' | 'country' | 'src' => {
    return ['description', 'country', 'src'].includes(key);
};

export const formInitialState: PhotoFormProps = {
    description: {
        value: "",
        error: false,
        errorMessage: "",
    },
    country: {
        value: "ru",
        error: false,
        errorMessage: "",
    },
    src: {
        value: "",
        error: false,
        errorMessage: "",
    }
};


export const formValidate = (formValues: PhotoFormProps): PhotoFormProps => {
    let newFormValues = { ...formValues };

    const fieldsToValidate = ['description', 'src', 'country'] as const;

    fieldsToValidate.forEach(field => {
            if (field in newFormValues) {
                const fieldValue = newFormValues[field];

                if (fieldValue && typeof fieldValue === 'object' && 'value' in fieldValue) {
                    let error = false;
                    let errorMessage = "";

                    switch (field) {
                        case 'description':
                            error = fieldValue.value?.length > MAX_PHOTO_DESCRIPTION_LENGTH;
                            errorMessage = error ? MAX_PHOTO_DESCRIPTION_ERROR : "";
                            break;
                        case 'src':
                            error = !Boolean(fieldValue.value);
                            errorMessage = error ? EMPTY_SRC_ERROR : "";
                            break;
                        case 'country':
                            error = !Boolean(fieldValue.value);
                            errorMessage = error ? EMPTY_COUNTRY_ERROR : "";
                            break;
                    }

                    newFormValues = {
                        ...newFormValues,
                        [field]: {
                            ...fieldValue,
                            error,
                            errorMessage
                        }
                    };
                }
            }
        });

        return newFormValues;
};

export const formHasErrors = (formValues: Record<string, any>) => {
    const formFields = ['description', 'src', 'country'] as const;

        return formFields.some((field) => {
            const fieldValue = formValues[field];
            return fieldValue &&
                   typeof fieldValue === 'object' &&
                   'error' in fieldValue &&
                   fieldValue.error === true;
        });
};