package com.maliga.application.views.team;

import com.maliga.application.data.Team;
import com.maliga.application.services.TeamService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("teams")
public class TeamView extends VerticalLayout {
    private final TeamService teamService;
    private Grid<Team> grid;
    private TextField nameFilter;
    private Button addButton;

    public TeamView(TeamService teamService) {
        this.teamService = teamService;
        createUI();
        updateList();
    }

    private void createUI() {
        // Create filter section
        nameFilter = new TextField("Filter by name");
        nameFilter.addValueChangeListener(e -> updateList());

        // Create grid
        grid = new Grid<>(Team.class);

        // Remove the default columns
        grid.removeAllColumns();

        // Add columns including the action column
        grid.addColumn(Team::getName).setHeader("Name");
        grid.addColumn(Team::getLogoUrl).setHeader("Logo URL");

        // Add an action column with edit and delete buttons
        grid.addComponentColumn(team -> {
            // Create a horizontal layout for our buttons
            HorizontalLayout actions = new HorizontalLayout();

            // Create edit button
            Button editButton = new Button("Edit", VaadinIcon.EDIT.create());
            editButton.addClickListener(e -> openTeamDialog(team));

            // Create delete button
            Button deleteButton = new Button("Delete", VaadinIcon.TRASH.create());
            deleteButton.addClickListener(e -> {
                // Add confirmation dialog before deleting
                ConfirmDialog dialog = new ConfirmDialog(
                        "Confirm delete",
                        "Are you sure you want to delete this team?",
                        "Delete", // confirmText
                        buttonClickEvent -> {
                            teamService.deleteTeam(team.getId());
                            updateList();
                        },
                        "Cancel", // cancelText
                        buttonClickEvent -> {
                        } // Do nothing on cancel
                );
                dialog.open();
            });

            // Style our buttons
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);

            actions.add(editButton, deleteButton);
            return actions;
        }).setHeader("Actions").setFlexGrow(0);

        // Create add button
        addButton = new Button("Add Team", VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openTeamDialog(new Team()));

        // Add components to the layout
        add(
                new H2("Team Management"),
                nameFilter,
                grid,
                addButton
        );
    }

    private void updateList() {
        grid.setItems(teamService.getAllTeams());
    }

    private void openTeamDialog(Team team) {
        // Create dialog for adding/editing teams
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(team.getId() == null ? "Add New Team" : "Edit Team");

        // Create form layout
        FormLayout formLayout = new FormLayout();

        TextField nameField = new TextField("Team Name");
        nameField.setValue(team.getName() != null ? team.getName() : "");
        nameField.setRequired(true);

        TextField logoUrlField = new TextField("Logo URL");
        logoUrlField.setValue(team.getLogoUrl() != null ? team.getLogoUrl() : "");

        formLayout.add(nameField, logoUrlField);

        // Create button layout
        HorizontalLayout buttonLayout = new HorizontalLayout();

        Button saveButton = new Button("Save", e -> {
            if (nameField.getValue().trim().isEmpty()) {
                Notification.show("Team name is required", 3000, Notification.Position.MIDDLE);
                return;
            }

            team.setName(nameField.getValue());
            team.setLogoUrl(logoUrlField.getValue());
            teamService.createTeam(team);
            updateList();
            dialog.close();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        buttonLayout.add(saveButton, cancelButton);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        // Add components to dialog
        dialog.add(formLayout, buttonLayout);

        dialog.open();
    }
}
