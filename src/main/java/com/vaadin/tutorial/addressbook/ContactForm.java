package com.vaadin.tutorial.addressbook;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.tutorial.addressbook.backend.Contact;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

/* Create your own Vaadin components by inheritance and composition.
 * This is a form component inherited from VerticalLayout. Use
 * Use BeanFieldGroup to bind data fields from DTO to UI fields.
 * Similarly named field by naming convention or customized
 * with @PropertyId annotation.
 */
public class ContactForm extends FormLayout {

	Button save = new Button("Save", this::save) {{
        /* Highlight primary actions.
		 * With Vaadin built-in styles you can highlight the primary save button
		 * and give it a keyboard shortcut for a better UX.
		 */
        setStyleName(ValoTheme.BUTTON_PRIMARY);
        setClickShortcut(ShortcutAction.KeyCode.ENTER);
    }};
	Button cancel = new Button("Cancel", this::cancel);

	TextField firstName = new TextField("First name");
	TextField lastName = new TextField("Last name");
	TextField phone = new TextField("Phone");
	TextField email = new TextField("Email");
	DateField birthDate = new DateField("Birth date");

	Contact contact;

    // Easily bind forms to beans and manage validation and buffering
    BeanFieldGroup<Contact> formFieldBindings;

    public ContactForm() {
        buildLayout();
        setVisible(false);
    }

    private void buildLayout() {
        setSizeUndefined();
        setMargin(true);

        HorizontalLayout actions = new HorizontalLayout(save, cancel);
        actions.setSpacing(true);

        addComponents(firstName, lastName, phone, email, birthDate, actions);
    }

    /*
     * Instead of using inline lambdas for event listeners like in
     * AddressbookUI, you can also implement listener methods in your
     * compositions or in separate controller classes and receive
     * to various Vaadin component events, like button clicks.
     */
	public void save(Button.ClickEvent event) {
        try {
            // Commit the fields from UI to DAO
            formFieldBindings.commit();

            // Save DAO to backend with direct synchronous service API
            getUI().service.save(contact);

            Notification.show("Saved: " + contact.getFirstName() + " " + contact.getLastName(),
                    Type.TRAY_NOTIFICATION);
            getUI().updateContactList();
        } catch (FieldGroup.CommitException e) {
            // Validation exceptions could be shown here
        }
	}

	public void cancel(Button.ClickEvent event) {
		// Place to call business logic.
		Notification.show("Cancelled", Type.TRAY_NOTIFICATION);
        getUI().contactList.select(null);
    }

    void edit(Contact contact) {
		this.contact = contact;
        if(contact != null) {
            // Bind the properties of the contact POJO to fiels in this form
            formFieldBindings = BeanFieldGroup.bindFieldsBuffered(contact, this);
            firstName.focus();
        }
        setVisible(contact != null);
	}

    @Override
    public AddressbookUI getUI() {
        return (AddressbookUI) super.getUI();
    }

}
