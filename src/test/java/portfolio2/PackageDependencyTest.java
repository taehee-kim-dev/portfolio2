package portfolio2;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = Application.class)
public class PackageDependencyTest {

    private static final String JAVA_LANG_OBJECT = "..java.lang..";

    private static final String ACCOUNT = "..module.account..";
    private static final String MAIN = "..module.main..";
    private static final String NOTIFICATION = "..module.notification..";
    private static final String POST = "..module.post..";
    private static final String SEARCH = "..module.search..";
    private static final String TAG = "..module.tag..";
    private static final String TEST = "..module.test..";

    @ArchTest
    ArchRule modulePackageRule = classes().that().resideInAPackage("portfolio2.module..")
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage("portfolio2.module..");


    @ArchTest
    ArchRule postPackageRuleAccessedRule = classes().that().resideInAPackage(POST)
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage(POST, SEARCH, TEST);


    @ArchTest
    ArchRule accountPackageAccessRule = classes().that().resideInAPackage(ACCOUNT)
            .should().accessClassesThat().resideInAnyPackage(ACCOUNT, JAVA_LANG_OBJECT, TAG);

    @ArchTest
    ArchRule mainPackageAccessRule = classes().that().resideInAPackage(MAIN)
            .should().accessClassesThat().resideInAnyPackage(MAIN, JAVA_LANG_OBJECT);

    @ArchTest
    ArchRule notificationPackageAccessRule = classes().that().resideInAPackage(NOTIFICATION)
            .should().accessClassesThat().resideInAnyPackage(NOTIFICATION, JAVA_LANG_OBJECT, ACCOUNT, TAG);

    @ArchTest
    ArchRule postPackageAccessRule = classes().that().resideInAPackage(POST)
            .should().accessClassesThat().resideInAnyPackage(POST, JAVA_LANG_OBJECT, ACCOUNT, TAG);

    @ArchTest
    ArchRule searchPackageAccessRule = classes().that().resideInAPackage(SEARCH)
            .should().accessClassesThat().resideInAnyPackage(SEARCH, JAVA_LANG_OBJECT, POST);

    @ArchTest
    ArchRule tagPackageAccessRule = classes().that().resideInAPackage(TAG)
            .should().accessClassesThat().resideInAnyPackage(TAG);

    @ArchTest
    ArchRule testPackageAccessRule = classes().that().resideInAPackage(TEST)
            .should().accessClassesThat().resideInAnyPackage(TEST, JAVA_LANG_OBJECT, POST);



    @ArchTest
    ArchRule cycleCheck = slices().matching("portfolio2.module.(*)..")
            .should().beFreeOfCycles();
}