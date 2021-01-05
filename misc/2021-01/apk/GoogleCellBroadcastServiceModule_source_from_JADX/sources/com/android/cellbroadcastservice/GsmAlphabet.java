package com.android.cellbroadcastservice;

import android.util.Log;
import android.util.SparseIntArray;

public class GsmAlphabet {
    private static SparseIntArray[] sCharsToGsmTables;
    private static SparseIntArray[] sCharsToShiftTables;
    private static final String[] sLanguageShiftTables;
    private static final String[] sLanguageTables;

    static {
        String[] strArr = {"@£$¥èéùìòÇ\nØø\rÅåΔ_ΦΓΛΩΠΨΣΘΞ￿ÆæßÉ !\"#¤%&'()*+,-./0123456789:;<=>?¡ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÑÜ§¿abcdefghijklmnopqrstuvwxyzäöñüà", "@£$¥€éùıòÇ\nĞğ\rÅåΔ_ΦΓΛΩΠΨΣΘΞ￿ŞşßÉ !\"#¤%&'()*+,-./0123456789:;<=>?İABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÑÜ§çabcdefghijklmnopqrstuvwxyzäöñüà", "", "@£$¥êéúíóç\nÔô\rÁáΔ_ªÇÀ∞^\\€Ó|￿ÂâÊÉ !\"#º%&'()*+,-./0123456789:;<=>?ÍABCDEFGHIJKLMNOPQRSTUVWXYZÃÕÚÜ§~abcdefghijklmnopqrstuvwxyzãõ`üà", "ঁংঃঅআইঈউঊঋ\nঌ \r এঐ  ওঔকখগঘঙচ￿ছজঝঞ !টঠডঢণত)(থদ,ধ.ন0123456789:; পফ?বভমযর ল   শষসহ়ঽািীুূৃৄ  েৈ  োৌ্ৎabcdefghijklmnopqrstuvwxyzৗড়ঢ়ৰৱ", "ઁંઃઅઆઇઈઉઊઋ\nઌઍ\r એઐઑ ઓઔકખગઘઙચ￿છજઝઞ !ટઠડઢણત)(થદ,ધ.ન0123456789:; પફ?બભમયર લળ વશષસહ઼ઽાિીુૂૃૄૅ ેૈૉ ોૌ્ૐabcdefghijklmnopqrstuvwxyzૠૡૢૣ૱", "ँंःअआइईउऊऋ\nऌऍ\rऎएऐऑऒओऔकखगघङच￿छजझञ !टठडढणत)(थद,ध.न0123456789:;ऩपफ?बभमयरऱलळऴवशषसह़ऽािीुूृॄॅॆेैॉॊोौ्ॐabcdefghijklmnopqrstuvwxyzॲॻॼॾॿ", " ಂಃಅಆಇಈಉಊಋ\nಌ \rಎಏಐ ಒಓಔಕಖಗಘಙಚ￿ಛಜಝಞ !ಟಠಡಢಣತ)(ಥದ,ಧ.ನ0123456789:; ಪಫ?ಬಭಮಯರಱಲಳ ವಶಷಸಹ಼ಽಾಿೀುೂೃೄ ೆೇೈ ೊೋೌ್ೕabcdefghijklmnopqrstuvwxyzೖೠೡೢೣ", " ംഃഅആഇഈഉഊഋ\nഌ \rഎഏഐ ഒഓഔകഖഗഘങച￿ഛജഝഞ !ടഠഡഢണത)(ഥദ,ധ.ന0123456789:; പഫ?ബഭമയരറലളഴവശഷസഹ ഽാിീുൂൃൄ െേൈ ൊോൌ്ൗabcdefghijklmnopqrstuvwxyzൠൡൢൣ൹", "ଁଂଃଅଆଇଈଉଊଋ\nଌ \r ଏଐ  ଓଔକଖଗଘଙଚ￿ଛଜଝଞ !ଟଠଡଢଣତ)(ଥଦ,ଧ.ନ0123456789:; ପଫ?ବଭମଯର ଲଳ ଵଶଷସହ଼ଽାିୀୁୂୃୄ  େୈ  ୋୌ୍ୖabcdefghijklmnopqrstuvwxyzୗୠୡୢୣ", "ਁਂਃਅਆਇਈਉਊ \n  \r ਏਐ  ਓਔਕਖਗਘਙਚ￿ਛਜਝਞ !ਟਠਡਢਣਤ)(ਥਦ,ਧ.ਨ0123456789:; ਪਫ?ਬਭਮਯਰ ਲਲ਼ ਵਸ਼ ਸਹ਼ ਾਿੀੁੂ    ੇੈ  ੋੌ੍ੑabcdefghijklmnopqrstuvwxyzੰੱੲੳੴ", " ஂஃஅஆஇஈஉஊ \n  \rஎஏஐ ஒஓஔக   ஙச￿ ஜ ஞ !ட   ணத)(  , .ந0123456789:;னப ?  மயரறலளழவஶஷஸஹ  ாிீுூ   ெேை ொோௌ்ௐabcdefghijklmnopqrstuvwxyzௗ௰௱௲௹", "ఁంఃఅఆఇఈఉఊఋ\nఌ \rఎఏఐ ఒఓఔకఖగఘఙచ￿ఛజఝఞ !టఠడఢణత)(థద,ధ.న0123456789:; పఫ?బభమయరఱలళ వశషసహ ఽాిీుూృౄ ెేై ొోౌ్ౕabcdefghijklmnopqrstuvwxyzౖౠౡౢౣ", "اآبٻڀپڦتۂٿ\nٹٽ\rٺټثجځڄڃڅچڇحخد￿ڌڈډڊ !ڏڍذرڑړ)(ڙز,ږ.ژ0123456789:;ښسش?صضطظعفقکڪګگڳڱلمنںڻڼوۄەہھءیېےٍُِٗٔabcdefghijklmnopqrstuvwxyzّٰٕٖٓ"};
        sLanguageTables = strArr;
        String[] strArr2 = {"          \f         ^                   {}     \\            [~] |                                    €                          ", "          \f         ^                   {}     \\            [~] |      Ğ İ         Ş               ç € ğ ı         ş            ", "         ç\f         ^                   {}     \\            [~] |Á       Í     Ó     Ú           á   €   í     ó     ú          ", "     ê   ç\fÔô Áá  ΦΓ^ΩΠΨΣΘ     Ê        {}     \\            [~] |À       Í     Ó     Ú     ÃÕ    Â   €   í     ó     ú     ãõ  â", "@£$¥¿\"¤%&'\f*+ -/<=>¡^¡_#*০১ ২৩৪৫৬৭৮৯য়ৠৡৢ{}ৣ৲৳৴৵\\৶৷৸৹৺       [~] |ABCDEFGHIJKLMNOPQRSTUVWXYZ          €                          ", "@£$¥¿\"¤%&'\f*+ -/<=>¡^¡_#*।॥ ૦૧૨૩૪૫૬૭૮૯  {}     \\            [~] |ABCDEFGHIJKLMNOPQRSTUVWXYZ          €                          ", "@£$¥¿\"¤%&'\f*+ -/<=>¡^¡_#*।॥ ०१२३४५६७८९॒॑{}॓॔क़ख़ग़\\ज़ड़ढ़फ़य़ॠॡॢॣ॰ॱ [~] |ABCDEFGHIJKLMNOPQRSTUVWXYZ          €                          ", "@£$¥¿\"¤%&'\f*+ -/<=>¡^¡_#*।॥ ೦೧೨೩೪೫೬೭೮೯ೞೱ{}ೲ    \\            [~] |ABCDEFGHIJKLMNOPQRSTUVWXYZ          €                          ", "@£$¥¿\"¤%&'\f*+ -/<=>¡^¡_#*।॥ ൦൧൨൩൪൫൬൭൮൯൰൱{}൲൳൴൵ൺ\\ൻർൽൾൿ       [~] |ABCDEFGHIJKLMNOPQRSTUVWXYZ          €                          ", "@£$¥¿\"¤%&'\f*+ -/<=>¡^¡_#*।॥ ୦୧୨୩୪୫୬୭୮୯ଡ଼ଢ଼{}ୟ୰ୱ  \\            [~] |ABCDEFGHIJKLMNOPQRSTUVWXYZ          €                          ", "@£$¥¿\"¤%&'\f*+ -/<=>¡^¡_#*।॥ ੦੧੨੩੪੫੬੭੮੯ਖ਼ਗ਼{}ਜ਼ੜਫ਼ੵ \\            [~] |ABCDEFGHIJKLMNOPQRSTUVWXYZ          €                          ", "@£$¥¿\"¤%&'\f*+ -/<=>¡^¡_#*।॥ ௦௧௨௩௪௫௬௭௮௯௳௴{}௵௶௷௸௺\\            [~] |ABCDEFGHIJKLMNOPQRSTUVWXYZ          €                          ", "@£$¥¿\"¤%&'\f*+ -/<=>¡^¡_#*   ౦౧౨౩౪౫౬౭౮౯ౘౙ{}౸౹౺౻౼\\౽౾౿         [~] |ABCDEFGHIJKLMNOPQRSTUVWXYZ          €                          ", "@£$¥¿\"¤%&'\f*+ -/<=>¡^¡_#*؀؁ ۰۱۲۳۴۵۶۷۸۹،؍{}؎؏ؐؑؒ\\ؓؔ؛؟ـْ٘٫٬ٲٳۍ[~]۔|ABCDEFGHIJKLMNOPQRSTUVWXYZ          €                          "};
        sLanguageShiftTables = strArr2;
        int length = strArr.length;
        int length2 = strArr2.length;
        if (length != length2) {
            Log.e("GSM", "Error: language tables array length " + length + " != shift tables array length " + length2);
        }
        sCharsToGsmTables = new SparseIntArray[length];
        for (int i = 0; i < length; i++) {
            String str = sLanguageTables[i];
            int length3 = str.length();
            if (!(length3 == 0 || length3 == 128)) {
                Log.e("GSM", "Error: language tables index " + i + " length " + length3 + " (expected 128 or 0)");
            }
            SparseIntArray sparseIntArray = new SparseIntArray(length3);
            sCharsToGsmTables[i] = sparseIntArray;
            for (int i2 = 0; i2 < length3; i2++) {
                sparseIntArray.put(str.charAt(i2), i2);
            }
        }
        sCharsToShiftTables = new SparseIntArray[length2];
        for (int i3 = 0; i3 < length2; i3++) {
            String str2 = sLanguageShiftTables[i3];
            int length4 = str2.length();
            if (!(length4 == 0 || length4 == 128)) {
                Log.e("GSM", "Error: language shift tables index " + i3 + " length " + length4 + " (expected 128 or 0)");
            }
            SparseIntArray sparseIntArray2 = new SparseIntArray(length4);
            sCharsToShiftTables[i3] = sparseIntArray2;
            for (int i4 = 0; i4 < length4; i4++) {
                char charAt = str2.charAt(i4);
                if (charAt != ' ') {
                    sparseIntArray2.put(charAt, i4);
                }
            }
        }
    }

    public static String gsm7BitPackedToString(byte[] bArr, int i, int i2) {
        return gsm7BitPackedToString(bArr, i, i2, 0, 0, 0);
    }

    public static String gsm7BitPackedToString(byte[] bArr, int i, int i2, int i3, int i4, int i5) {
        int i6 = i2;
        int i7 = i4;
        int i8 = i5;
        String[] strArr = sLanguageShiftTables;
        String[] strArr2 = sLanguageTables;
        StringBuilder sb = new StringBuilder(i6);
        if (i7 < 0 || i7 > strArr2.length) {
            Log.w("GSM", "unknown language table " + i7 + ", using default");
            i7 = 0;
        }
        if (i8 < 0 || i8 > strArr.length) {
            Log.w("GSM", "unknown single shift table " + i8 + ", using default");
            i8 = 0;
        }
        try {
            String str = strArr2[i7];
            String str2 = strArr[i8];
            if (str.isEmpty()) {
                Log.w("GSM", "no language table for code " + i7 + ", using default");
                str = strArr2[0];
            }
            if (str2.isEmpty()) {
                Log.w("GSM", "no single shift table for code " + i8 + ", using default");
                str2 = strArr[0];
            }
            boolean z = false;
            for (int i9 = 0; i9 < i6; i9++) {
                int i10 = (i9 * 7) + i3;
                int i11 = i10 / 8;
                int i12 = i10 % 8;
                int i13 = i11 + i;
                int i14 = (bArr[i13] >> i12) & 127;
                if (i12 > 1) {
                    i14 = (i14 & (127 >> (i12 - 1))) | ((bArr[i13 + 1] << (8 - i12)) & 127);
                }
                if (z) {
                    if (i14 == 27) {
                        sb.append(' ');
                    } else {
                        char charAt = str2.charAt(i14);
                        if (charAt == ' ') {
                            sb.append(str.charAt(i14));
                        } else {
                            sb.append(charAt);
                        }
                    }
                    z = false;
                } else if (i14 == 27) {
                    z = true;
                } else {
                    sb.append(str.charAt(i14));
                }
            }
            return sb.toString();
        } catch (RuntimeException e) {
            Log.e("GSM", "Error GSM 7 bit packed: ", e);
            return null;
        }
    }
}
