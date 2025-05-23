package com.example.safetyandcheap.ui.util

enum class MainNavRoutes(val route: String, val group: MainNavGroups) {
    ListFlow("listFlow", MainNavGroups.None),
    ListMenu("listMenu", MainNavGroups.List),
    PropertyScreen("HouseMenu", MainNavGroups.None),
    Filtration("Filtration", MainNavGroups.Filtration),
    Profile("Profile", MainNavGroups.Profile),
    PersonalDataFill("PersonalDataFill", MainNavGroups.PersonalData),
    NewAnnouncement("NewAnnouncement", MainNavGroups.NewAnnouncement),
    NewAnnouncementChooseDealType("NewAnnouncementChooseDealType", MainNavGroups.NewAnnouncement),
    NewAnnouncementChoosePropertyType(
        "NewAnnouncementChoosePropertyType",
        MainNavGroups.NewAnnouncement
    ),
    NewAnnouncementFill("NewAnnouncementFill", MainNavGroups.NewAnnouncement),
    AnnouncementEdit("NewAnnouncementEdit", MainNavGroups.EditAnnouncement),
    Cart("CartMenu", MainNavGroups.Cart),
    YourAnnouncements("YourAnnouncements", MainNavGroups.YourAnnouncements),
    PhoneNumberFlow("PhoneNumberFlow", MainNavGroups.None),
    NewAnnouncementFlow("NewAnnouncementFlow", MainNavGroups.None),
    AddPhoneNumber("AddPhoneNumber", MainNavGroups.None),
    VerifyPhoneNumber("VerifyPhoneNumber", MainNavGroups.None);

    companion object {
        fun getByRoute(name: String?): MainNavRoutes? {
            return entries.find { it.route.equals(name?.split('/')[0], ignoreCase = false) }
        }
    }
}

enum class MainNavGroups(val localName: String) {
    YourAnnouncements("Your announcements"),
    List("Announcements"),
    Filtration("Filter"),
    NewAnnouncement("New announcement"),
    Profile("Profile"),
    PersonalData("Personal data"),
    EditAnnouncement("Edit announcement"),
    Cart("Your cart"),
    None("")
}