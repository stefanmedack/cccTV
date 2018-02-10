package de.stefanmedack.ccctv.model

enum class ConferenceGroup(val slugPrefix: String) {

    CONGRESS("congress"),
    CAMP("conferences/camp"),

    // Think about creating a category for this (and more) CCC related formats
    //    CHAOS_OPENCHAOS("events/openchaos"),
    //    CHAOS_DATENGARTEN("events/datengarten"),
    //    CHAOS_RADIO("broadcast/chaosradio"),

    CRYPTOCON("conferences/cryptocon"),
    DATENSPUREN("conferences/datenspuren"),
    DENOG("conferences/denog"),
    EH("conferences/eh"),
    FIFFKON("conferences/fiffkon"),
    FROSCON("conferences/froscon"),
    GPN("conferences/gpn"),
    HACKOVER("conferences/hackover"),
    JUGENDHACKT("events/jugendhackt"),
    MRMCD("conferences/mrmcd"),
    NETZPOLITIK("conferences/netzpolitik"),
    OSC("conferences/osc"),
    SIGINT("conferences/sigint"),
    VCFB("conferences/vcfb"),

    OTHER_CONFERENCES("conferences"),
    //    OTHER_EVENTS("events"), // for now we merge OTHER and OTHER_EVENTS
    OTHER("")

}