(ns nukr.test-data
  (:require 
   [clojure.test :refer :all]
   [nukr.controller :as controller]
   [nukr.database :as database]))

(defn build-sample-network
  []
  (database/clear!)
  ;; names taken from https://www.randomlists.com/
  (let [jon-bentley (controller/add-profile!
                     {:name "Jon Bentley"
                      :email "jon.bentley@nukr.com"
                      :visible false})
        zaria-wise (controller/add-profile!
                    {:name "Zaria Wise"
                     :email "zaria.wise@nukr.com"
                     :visible true})
        cadence-goodman (controller/add-profile!
                         {:name "Cadence Goodman"
                          :email "cadence.goodman@nukr.com"
                          :visible false})
        hayden-odom (controller/add-profile!
                     {:name "Hayden Odom"
                      :email "hayden.odom@nukr.com"
                      :visible true})
        jocelyn-larson (controller/add-profile!
                        {:name "Jocelyn Larson"
                         :email "jocelyn.larson@nukr.com"
                         :visible true})
        tyshawn-diaz (controller/add-profile!
                      {:name "Tyshawn Diaz"
                       :email "tyshawn.diaz@nukr.com"
                       :visible true})
        luz-richardson (controller/add-profile!
                        {:name "Luz Richardson"
                         :email "luz.richardson@nukr.com"
                         :visible true})
        leticia-clements (controller/add-profile!
                          {:name "Leticia Clements"
                           :email "leticia.clements@nukr.com"
                           :visible true})
        ben-patterson (controller/add-profile!
                       {:name "Ben Patterson"
                        :email "ben.patterson@nukr.com"
                        :visible true})
        rodolfo-bird (controller/add-profile!
                      {:name "Rodolfo Bird"
                       :email "rodolfo.bird@nukr.com"
                       :visible true})
        kendra-heath (controller/add-profile!
                      {:name "Kendra Heath"
                       :email "kendra.heath@nukr.com"
                       :visible true})
        cheyanne-mccullough (controller/add-profile!
                             {:name "Cheyanne Mccullough"
                              :email "cheyanne.mccullough@nukr.com"
                              :visible true})
        rhianna-grant (controller/add-profile!
                       {:name "Rhianna Grant"
                        :email "rhianna.grant@nukr.com"
                        :visible true})
        sadie-hunt (controller/add-profile!
                    {:name "Sadie Hunt"
                     :email "sadie.hunt@nukr.com"
                     :visible true})
        riya-maynard (controller/add-profile!
                      {:name "Riya Maynard"
                       :email "riya.maynard@nukr.com"
                       :visible true})
        emilio-whitaker (controller/add-profile!
                         {:name "Emilio Whitaker"
                          :email "emilio.whitaker@nukr.com"
                          :visible true})
        anton-hardy (controller/add-profile!
                     {:name "Anton Hardy"
                      :email "anton.hardy@nukr.com"
                      :visible false})
        luna-cervantes (controller/add-profile!
                        {:name "Luna Cervantes"
                         :email "luna.cervantes@nukr.com"
                         :visible true})
        terry-thompson (controller/add-profile!
                        {:name "Terry Thompson"
                         :email "terry.thompson@nukr.com"
                         :visible true})
        griffin-valentine (controller/add-profile!
                           {:name "Griffin Valentine"
                            :email "griffin.valentine@nukr.com"
                            :visible true})
        karen-robles (controller/add-profile!
                      {:name "Karen Robles"
                       :email "karen.robles@nukr.com"
                       :visible true})
        jack-velasquez (controller/add-profile!
                        {:name "Jack Velasquez"
                         :email "jack.velasquez@nukr.com"
                         :visible true})
        aidan-carey (controller/add-profile!
                     {:name "Aidan Carey"
                      :email "aidan.carey@nukr.com"
                      :visible true})
        gracelyn-turner (controller/add-profile!
                         {:name "Gracelyn Turner"
                          :email "gracelyn.turner@nukr.com"
                          :visible false})
        talia-benson (controller/add-profile!
                      {:name "Talia Benson"
                       :email "talia.benson@nukr.com"
                       :visible false})
        darnell-blake (controller/add-profile!
                       {:name "Darnell Blake"
                        :email "darnell.blake@nukr.com"
                        :visible false})
        delaney-greer (controller/add-profile!
                       {:name "Delaney Greer"
                        :email "delaney.greer@nukr.com"
                        :visible true})
        noe-burton (controller/add-profile!
                    {:name "Noe Burton"
                     :email "noe.burton@nukr.com"
                     :visible true})
        brett-goodwin (controller/add-profile!
                       {:name "Brett Goodwin"
                        :email "brett.goodwin@nukr.com"
                        :visible false})
        melvin-aguilar (controller/add-profile!
                        {:name "Melvin Aguilar"
                         :email "melvin.aguilar@nukr.com"
                         :visible true})
        rylan-mooney (controller/add-profile!
                      {:name "Rylan Mooney"
                       :email "rylan.mooney@nukr.com"
                       :visible true})
        kaylin-fitzpatrick (controller/add-profile!
                            {:name "Kaylin Fitzpatrick"
                             :email "kaylin.fitzpatrick@nukr.com"
                             :visible true})
        malcolm-zhang (controller/add-profile!
                       {:name "Malcolm Zhang"
                        :email "malcolm.zhang@nukr.com"
                        :visible false})
        alexis-bush (controller/add-profile!
                     {:name "Alexis Bush"
                      :email "alexis.bush@nukr.com"
                      :visible true})
        zayne-clements (controller/add-profile!
                        {:name "Zayne Clements"
                         :email "zayne.clements@nukr.com"
                         :visible false})
        emiliano-alvarado (controller/add-profile!
                           {:name "Emiliano Alvarado"
                            :email "emiliano.alvarado@nukr.com"
                            :visible true})
        brody-garrett (controller/add-profile!
                       {:name "Brody Garrett"
                        :email "brody.garrett@nukr.com"
                        :visible false})
        paisley-rollins (controller/add-profile!
                         {:name "Paisley Rollins"
                          :email "paisley.rollins@nukr.com"
                          :visible true})
        alden-massey (controller/add-profile!
                      {:name "Alden Massey"
                       :email "alden.massey@nukr.com"
                       :visible true})
        brodie-gilmore (controller/add-profile!
                        {:name "Brodie Gilmore"
                         :email "brodie.gilmore@nukr.com"
                         :visible true})
        kaydence-davidson (controller/add-profile!
                           {:name "Kaydence Davidson"
                            :email "kaydence.davidson@nukr.com"
                            :visible true})
        lennon-mason (controller/add-profile!
                      {:name "Lennon Mason"
                       :email "lennon.mason@nukr.com"
                       :visible false})
        madisyn-braun (controller/add-profile!
                       {:name "Madisyn Braun"
                        :email "madisyn.braun@nukr.com"
                        :visible false})
        ella-tyler (controller/add-profile!
                    {:name "Ella Tyler"
                     :email "ella.tyler@nukr.com"
                     :visible false})
        paul-moran (controller/add-profile!
                    {:name "Paul Moran"
                     :email "paul.moran@nukr.com"
                     :visible false})
        griffin-cervantes (controller/add-profile!
                           {:name "Griffin Cervantes"
                            :email "griffin.cervantes@nukr.com"
                            :visible false})
        maliyah-wilcox (controller/add-profile!
                        {:name "Maliyah Wilcox"
                         :email "maliyah.wilcox@nukr.com"
                         :visible false})
        tristen-joyce (controller/add-profile!
                       {:name "Tristen Joyce"
                        :email "tristen.joyce@nukr.com"
                        :visible true})
        rhett-shepard (controller/add-profile!
                       {:name "Rhett Shepard"
                        :email "rhett.shepard@nukr.com"
                        :visible true})
        shelby-austin (controller/add-profile!
                       {:name "Shelby Austin"
                        :email "shelby.austin@nukr.com"
                        :visible true})]
    (controller/connect-profiles! (:id jon-bentley) (:id gracelyn-turner))
    (controller/connect-profiles! (:id jon-bentley) (:id darnell-blake))
    (controller/connect-profiles! (:id zaria-wise) (:id ella-tyler))
    (controller/connect-profiles! (:id zaria-wise) (:id brodie-gilmore))
    (controller/connect-profiles! (:id zaria-wise) (:id shelby-austin))
    (controller/connect-profiles! (:id zaria-wise) (:id delaney-greer))
    (controller/connect-profiles! (:id zaria-wise) (:id noe-burton))
    (controller/connect-profiles! (:id zaria-wise) (:id kendra-heath))
    (controller/connect-profiles! (:id zaria-wise) (:id paul-moran))
    (controller/connect-profiles! (:id zaria-wise) (:id kaylin-fitzpatrick))
    (controller/connect-profiles! (:id zaria-wise) (:id talia-benson))
    (controller/connect-profiles! (:id cadence-goodman) (:id malcolm-zhang))
    (controller/connect-profiles! (:id cadence-goodman) (:id talia-benson))
    (controller/connect-profiles! (:id cadence-goodman) (:id karen-robles))
    (controller/connect-profiles! (:id cadence-goodman) (:id ben-patterson))
    (controller/connect-profiles! (:id cadence-goodman) (:id brody-garrett))
    (controller/connect-profiles! (:id cadence-goodman) (:id darnell-blake))
    (controller/connect-profiles! (:id hayden-odom) (:id anton-hardy))
    (controller/connect-profiles! (:id hayden-odom) (:id madisyn-braun))
    (controller/connect-profiles! (:id hayden-odom) (:id jocelyn-larson))
    (controller/connect-profiles! (:id hayden-odom) (:id maliyah-wilcox))
    (controller/connect-profiles! (:id hayden-odom) (:id kaylin-fitzpatrick))
    (controller/connect-profiles! (:id hayden-odom) (:id cheyanne-mccullough))
    (controller/connect-profiles! (:id jocelyn-larson) (:id kaydence-davidson))
    (controller/connect-profiles! (:id jocelyn-larson) (:id terry-thompson))
    (controller/connect-profiles! (:id jocelyn-larson) (:id griffin-cervantes))
    (controller/connect-profiles! (:id tyshawn-diaz) (:id kendra-heath))
    (controller/connect-profiles! (:id tyshawn-diaz) (:id paul-moran))
    (controller/connect-profiles! (:id tyshawn-diaz) (:id cheyanne-mccullough))
    (controller/connect-profiles! (:id tyshawn-diaz) (:id brody-garrett))
    (controller/connect-profiles! (:id tyshawn-diaz) (:id karen-robles))
    (controller/connect-profiles! (:id luz-richardson) (:id cheyanne-mccullough))
    (controller/connect-profiles! (:id luz-richardson) (:id kendra-heath))
    (controller/connect-profiles! (:id leticia-clements) (:id brodie-gilmore))
    (controller/connect-profiles! (:id leticia-clements) (:id jocelyn-larson))
    (controller/connect-profiles! (:id leticia-clements) (:id brody-garrett))
    (controller/connect-profiles! (:id ben-patterson) (:id kendra-heath))
    (controller/connect-profiles! (:id ben-patterson) (:id rhett-shepard))
    (controller/connect-profiles! (:id ben-patterson) (:id madisyn-braun))
    (controller/connect-profiles! (:id ben-patterson) (:id tristen-joyce))
    (controller/connect-profiles! (:id ben-patterson) (:id aidan-carey))
    (controller/connect-profiles! (:id ben-patterson) (:id emiliano-alvarado))
    (controller/connect-profiles! (:id ben-patterson) (:id tyshawn-diaz))
    (controller/connect-profiles! (:id ben-patterson) (:id luz-richardson))
    (controller/connect-profiles! (:id ben-patterson) (:id brett-goodwin))
    (controller/connect-profiles! (:id ben-patterson) (:id shelby-austin))
    (controller/connect-profiles! (:id rodolfo-bird) (:id kaydence-davidson))
    (controller/connect-profiles! (:id rodolfo-bird) (:id alexis-bush))
    (controller/connect-profiles! (:id kendra-heath) (:id alexis-bush))
    (controller/connect-profiles! (:id kendra-heath) (:id rhianna-grant))
    (controller/connect-profiles! (:id kendra-heath) (:id ella-tyler))
    (controller/connect-profiles! (:id kendra-heath) (:id jack-velasquez))
    (controller/connect-profiles! (:id kendra-heath) (:id gracelyn-turner))
    (controller/connect-profiles! (:id kendra-heath) (:id brett-goodwin))
    (controller/connect-profiles! (:id kendra-heath) (:id jocelyn-larson))
    (controller/connect-profiles! (:id cheyanne-mccullough) (:id lennon-mason))
    (controller/connect-profiles! (:id cheyanne-mccullough) (:id kendra-heath))
    (controller/connect-profiles! (:id cheyanne-mccullough) (:id gracelyn-turner))
    (controller/connect-profiles! (:id cheyanne-mccullough) (:id terry-thompson))
    (controller/connect-profiles! (:id cheyanne-mccullough) (:id cadence-goodman))
    (controller/connect-profiles! (:id cheyanne-mccullough) (:id melvin-aguilar))
    (controller/connect-profiles! (:id rhianna-grant) (:id brody-garrett))
    (controller/connect-profiles! (:id rhianna-grant) (:id luna-cervantes))
    (controller/connect-profiles! (:id rhianna-grant) (:id kaydence-davidson))
    (controller/connect-profiles! (:id rhianna-grant) (:id anton-hardy))
    (controller/connect-profiles! (:id rhianna-grant) (:id jocelyn-larson))
    (controller/connect-profiles! (:id rhianna-grant) (:id malcolm-zhang))
    (controller/connect-profiles! (:id sadie-hunt) (:id zayne-clements))
    (controller/connect-profiles! (:id sadie-hunt) (:id rhett-shepard))
    (controller/connect-profiles! (:id sadie-hunt) (:id leticia-clements))
    (controller/connect-profiles! (:id sadie-hunt) (:id kendra-heath))
    (controller/connect-profiles! (:id sadie-hunt) (:id terry-thompson))
    (controller/connect-profiles! (:id riya-maynard) (:id rhianna-grant))
    (controller/connect-profiles! (:id riya-maynard) (:id shelby-austin))
    (controller/connect-profiles! (:id riya-maynard) (:id hayden-odom))
    (controller/connect-profiles! (:id riya-maynard) (:id paisley-rollins))
    (controller/connect-profiles! (:id riya-maynard) (:id luna-cervantes))
    (controller/connect-profiles! (:id emilio-whitaker) (:id rodolfo-bird))
    (controller/connect-profiles! (:id emilio-whitaker) (:id malcolm-zhang))
    (controller/connect-profiles! (:id emilio-whitaker) (:id brett-goodwin))
    (controller/connect-profiles! (:id emilio-whitaker) (:id gracelyn-turner))
    (controller/connect-profiles! (:id emilio-whitaker) (:id paisley-rollins))
    (controller/connect-profiles! (:id emilio-whitaker) (:id rhianna-grant))
    (controller/connect-profiles! (:id anton-hardy) (:id cheyanne-mccullough))
    (controller/connect-profiles! (:id anton-hardy) (:id lennon-mason))
    (controller/connect-profiles! (:id anton-hardy) (:id jocelyn-larson))
    (controller/connect-profiles! (:id anton-hardy) (:id brett-goodwin))
    (controller/connect-profiles! (:id anton-hardy) (:id rodolfo-bird))
    (controller/connect-profiles! (:id anton-hardy) (:id zayne-clements))
    (controller/connect-profiles! (:id anton-hardy) (:id delaney-greer))
    (controller/connect-profiles! (:id anton-hardy) (:id aidan-carey))
    (controller/connect-profiles! (:id luna-cervantes) (:id shelby-austin))
    (controller/connect-profiles! (:id luna-cervantes) (:id tristen-joyce))
    (controller/connect-profiles! (:id luna-cervantes) (:id brett-goodwin))
    (controller/connect-profiles! (:id terry-thompson) (:id aidan-carey))
    (controller/connect-profiles! (:id terry-thompson) (:id kaylin-fitzpatrick))
    (controller/connect-profiles! (:id terry-thompson) (:id luna-cervantes))
    (controller/connect-profiles! (:id terry-thompson) (:id zaria-wise))
    (controller/connect-profiles! (:id terry-thompson) (:id malcolm-zhang))
    (controller/connect-profiles! (:id terry-thompson) (:id rhett-shepard))
    (controller/connect-profiles! (:id terry-thompson) (:id brett-goodwin))
    (controller/connect-profiles! (:id terry-thompson) (:id gracelyn-turner))
    (controller/connect-profiles! (:id griffin-valentine) (:id shelby-austin))
    (controller/connect-profiles! (:id karen-robles) (:id zaria-wise))
    (controller/connect-profiles! (:id karen-robles) (:id malcolm-zhang))
    (controller/connect-profiles! (:id karen-robles) (:id brett-goodwin))
    (controller/connect-profiles! (:id karen-robles) (:id luna-cervantes))
    (controller/connect-profiles! (:id karen-robles) (:id jon-bentley))
    (controller/connect-profiles! (:id karen-robles) (:id rhianna-grant))
    (controller/connect-profiles! (:id jack-velasquez) (:id ella-tyler))
    (controller/connect-profiles! (:id aidan-carey) (:id melvin-aguilar))
    (controller/connect-profiles! (:id gracelyn-turner) (:id maliyah-wilcox))
    (controller/connect-profiles! (:id gracelyn-turner) (:id paisley-rollins))
    (controller/connect-profiles! (:id gracelyn-turner) (:id anton-hardy))
    (controller/connect-profiles! (:id gracelyn-turner) (:id rhett-shepard))
    (controller/connect-profiles! (:id gracelyn-turner) (:id brett-goodwin))
    (controller/connect-profiles! (:id gracelyn-turner) (:id alden-massey))
    (controller/connect-profiles! (:id gracelyn-turner) (:id luz-richardson))
    (controller/connect-profiles! (:id gracelyn-turner) (:id leticia-clements))
    (controller/connect-profiles! (:id gracelyn-turner) (:id tyshawn-diaz))
    (controller/connect-profiles! (:id talia-benson) (:id tyshawn-diaz))
    (controller/connect-profiles! (:id talia-benson) (:id lennon-mason))
    (controller/connect-profiles! (:id talia-benson) (:id aidan-carey))
    (controller/connect-profiles! (:id darnell-blake) (:id sadie-hunt))
    (controller/connect-profiles! (:id darnell-blake) (:id shelby-austin))
    (controller/connect-profiles! (:id darnell-blake) (:id jocelyn-larson))
    (controller/connect-profiles! (:id darnell-blake) (:id griffin-cervantes))
    (controller/connect-profiles! (:id delaney-greer) (:id luz-richardson))
    (controller/connect-profiles! (:id delaney-greer) (:id malcolm-zhang))
    (controller/connect-profiles! (:id delaney-greer) (:id sadie-hunt))
    (controller/connect-profiles! (:id delaney-greer) (:id paul-moran))
    (controller/connect-profiles! (:id delaney-greer) (:id rodolfo-bird))
    (controller/connect-profiles! (:id delaney-greer) (:id maliyah-wilcox))
    (controller/connect-profiles! (:id delaney-greer) (:id aidan-carey))
    (controller/connect-profiles! (:id noe-burton) (:id delaney-greer))
    (controller/connect-profiles! (:id noe-burton) (:id lennon-mason))
    (controller/connect-profiles! (:id noe-burton) (:id luna-cervantes))
    (controller/connect-profiles! (:id noe-burton) (:id griffin-cervantes))
    (controller/connect-profiles! (:id noe-burton) (:id rhianna-grant))
    (controller/connect-profiles! (:id noe-burton) (:id paisley-rollins))
    (controller/connect-profiles! (:id noe-burton) (:id rodolfo-bird))
    (controller/connect-profiles! (:id noe-burton) (:id brody-garrett))
    (controller/connect-profiles! (:id brett-goodwin) (:id cadence-goodman))
    (controller/connect-profiles! (:id brett-goodwin) (:id tyshawn-diaz))
    (controller/connect-profiles! (:id brett-goodwin) (:id maliyah-wilcox))
    (controller/connect-profiles! (:id brett-goodwin) (:id shelby-austin))
    (controller/connect-profiles! (:id melvin-aguilar) (:id rylan-mooney))
    (controller/connect-profiles! (:id melvin-aguilar) (:id luz-richardson))
    (controller/connect-profiles! (:id melvin-aguilar) (:id darnell-blake))
    (controller/connect-profiles! (:id melvin-aguilar) (:id emilio-whitaker))
    (controller/connect-profiles! (:id melvin-aguilar) (:id terry-thompson))
    (controller/connect-profiles! (:id melvin-aguilar) (:id malcolm-zhang))
    (controller/connect-profiles! (:id melvin-aguilar) (:id jack-velasquez))
    (controller/connect-profiles! (:id melvin-aguilar) (:id hayden-odom))
    (controller/connect-profiles! (:id melvin-aguilar) (:id rodolfo-bird))
    (controller/connect-profiles! (:id melvin-aguilar) (:id griffin-valentine))
    (controller/connect-profiles! (:id melvin-aguilar) (:id jon-bentley))
    (controller/connect-profiles! (:id rylan-mooney) (:id paul-moran))
    (controller/connect-profiles! (:id kaylin-fitzpatrick) (:id alexis-bush))
    (controller/connect-profiles! (:id kaylin-fitzpatrick) (:id noe-burton))
    (controller/connect-profiles! (:id kaylin-fitzpatrick) (:id luna-cervantes))
    (controller/connect-profiles! (:id kaylin-fitzpatrick) (:id talia-benson))
    (controller/connect-profiles! (:id kaylin-fitzpatrick) (:id darnell-blake))
    (controller/connect-profiles! (:id malcolm-zhang) (:id leticia-clements))
    (controller/connect-profiles! (:id malcolm-zhang) (:id rylan-mooney))
    (controller/connect-profiles! (:id malcolm-zhang) (:id sadie-hunt))
    (controller/connect-profiles! (:id malcolm-zhang) (:id griffin-cervantes))
    (controller/connect-profiles! (:id malcolm-zhang) (:id brodie-gilmore))
    (controller/connect-profiles! (:id alexis-bush) (:id rhianna-grant))
    (controller/connect-profiles! (:id alexis-bush) (:id sadie-hunt))
    (controller/connect-profiles! (:id alexis-bush) (:id brody-garrett))
    (controller/connect-profiles! (:id alexis-bush) (:id kaydence-davidson))
    (controller/connect-profiles! (:id zayne-clements) (:id tristen-joyce))
    (controller/connect-profiles! (:id zayne-clements) (:id kaylin-fitzpatrick))
    (controller/connect-profiles! (:id zayne-clements) (:id terry-thompson))
    (controller/connect-profiles! (:id zayne-clements) (:id luz-richardson))
    (controller/connect-profiles! (:id zayne-clements) (:id ella-tyler))
    (controller/connect-profiles! (:id emiliano-alvarado) (:id aidan-carey))
    (controller/connect-profiles! (:id emiliano-alvarado) (:id griffin-valentine))
    (controller/connect-profiles! (:id emiliano-alvarado) (:id jack-velasquez))
    (controller/connect-profiles! (:id emiliano-alvarado) (:id shelby-austin))
    (controller/connect-profiles! (:id emiliano-alvarado) (:id luna-cervantes))
    (controller/connect-profiles! (:id emiliano-alvarado) (:id cheyanne-mccullough))
    (controller/connect-profiles! (:id brody-garrett) (:id paul-moran))
    (controller/connect-profiles! (:id brody-garrett) (:id brodie-gilmore))
    (controller/connect-profiles! (:id brody-garrett) (:id cheyanne-mccullough))
    (controller/connect-profiles! (:id brody-garrett) (:id malcolm-zhang))
    (controller/connect-profiles! (:id brody-garrett) (:id emilio-whitaker))
    (controller/connect-profiles! (:id brody-garrett) (:id darnell-blake))
    (controller/connect-profiles! (:id paisley-rollins) (:id hayden-odom))
    (controller/connect-profiles! (:id paisley-rollins) (:id zaria-wise))
    (controller/connect-profiles! (:id paisley-rollins) (:id cheyanne-mccullough))
    (controller/connect-profiles! (:id paisley-rollins) (:id delaney-greer))
    (controller/connect-profiles! (:id paisley-rollins) (:id emiliano-alvarado))
    (controller/connect-profiles! (:id alden-massey) (:id maliyah-wilcox))
    (controller/connect-profiles! (:id alden-massey) (:id sadie-hunt))
    (controller/connect-profiles! (:id alden-massey) (:id hayden-odom))
    (controller/connect-profiles! (:id alden-massey) (:id rhett-shepard))
    (controller/connect-profiles! (:id brodie-gilmore) (:id terry-thompson))
    (controller/connect-profiles! (:id brodie-gilmore) (:id jack-velasquez))
    (controller/connect-profiles! (:id brodie-gilmore) (:id sadie-hunt))
    (controller/connect-profiles! (:id brodie-gilmore) (:id rhianna-grant))
    (controller/connect-profiles! (:id brodie-gilmore) (:id maliyah-wilcox))
    (controller/connect-profiles! (:id brodie-gilmore) (:id kendra-heath))
    (controller/connect-profiles! (:id brodie-gilmore) (:id ella-tyler))
    (controller/connect-profiles! (:id brodie-gilmore) (:id emiliano-alvarado))
    (controller/connect-profiles! (:id kaydence-davidson) (:id rhett-shepard))
    (controller/connect-profiles! (:id kaydence-davidson) (:id brodie-gilmore))
    (controller/connect-profiles! (:id kaydence-davidson) (:id luna-cervantes))
    (controller/connect-profiles! (:id kaydence-davidson) (:id rylan-mooney))
    (controller/connect-profiles! (:id kaydence-davidson) (:id leticia-clements))
    (controller/connect-profiles! (:id lennon-mason) (:id delaney-greer))
    (controller/connect-profiles! (:id lennon-mason) (:id ben-patterson))
    (controller/connect-profiles! (:id lennon-mason) (:id malcolm-zhang))
    (controller/connect-profiles! (:id madisyn-braun) (:id talia-benson))
    (controller/connect-profiles! (:id madisyn-braun) (:id ella-tyler))
    (controller/connect-profiles! (:id madisyn-braun) (:id emilio-whitaker))
    (controller/connect-profiles! (:id madisyn-braun) (:id leticia-clements))
    (controller/connect-profiles! (:id madisyn-braun) (:id malcolm-zhang))
    (controller/connect-profiles! (:id madisyn-braun) (:id tyshawn-diaz))
    (controller/connect-profiles! (:id ella-tyler) (:id lennon-mason))
    (controller/connect-profiles! (:id ella-tyler) (:id shelby-austin))
    (controller/connect-profiles! (:id ella-tyler) (:id alexis-bush))
    (controller/connect-profiles! (:id ella-tyler) (:id alden-massey))
    (controller/connect-profiles! (:id ella-tyler) (:id emiliano-alvarado))
    (controller/connect-profiles! (:id ella-tyler) (:id rhett-shepard))
    (controller/connect-profiles! (:id paul-moran) (:id kaylin-fitzpatrick))
    (controller/connect-profiles! (:id paul-moran) (:id sadie-hunt))
    (controller/connect-profiles! (:id paul-moran) (:id madisyn-braun))
    (controller/connect-profiles! (:id paul-moran) (:id malcolm-zhang))
    (controller/connect-profiles! (:id paul-moran) (:id gracelyn-turner))
    (controller/connect-profiles! (:id paul-moran) (:id rhett-shepard))
    (controller/connect-profiles! (:id griffin-cervantes) (:id tristen-joyce))
    (controller/connect-profiles! (:id griffin-cervantes) (:id kendra-heath))
    (controller/connect-profiles! (:id griffin-cervantes) (:id maliyah-wilcox))
    (controller/connect-profiles! (:id maliyah-wilcox) (:id luz-richardson))
    (controller/connect-profiles! (:id maliyah-wilcox) (:id paisley-rollins))
    (controller/connect-profiles! (:id maliyah-wilcox) (:id jack-velasquez))
    (controller/connect-profiles! (:id maliyah-wilcox) (:id malcolm-zhang))
    (controller/connect-profiles! (:id tristen-joyce) (:id paul-moran))
    (controller/connect-profiles! (:id tristen-joyce) (:id luz-richardson))
    (controller/connect-profiles! (:id tristen-joyce) (:id delaney-greer))
    (controller/connect-profiles! (:id rhett-shepard) (:id delaney-greer))
    (controller/connect-profiles! (:id rhett-shepard) (:id cheyanne-mccullough))
    (controller/connect-profiles! (:id rhett-shepard) (:id madisyn-braun))
    (controller/connect-profiles! (:id rhett-shepard) (:id kendra-heath))
    (controller/connect-profiles! (:id shelby-austin) (:id cheyanne-mccullough))
    (controller/connect-profiles! (:id shelby-austin) (:id madisyn-braun))
    (controller/connect-profiles! (:id shelby-austin) (:id tyshawn-diaz))))