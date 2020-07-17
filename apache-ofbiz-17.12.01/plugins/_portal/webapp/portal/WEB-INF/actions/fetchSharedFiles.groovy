import org.apache.ofbiz.entity.util.EntityQuery
import org.apache.ofbiz.entity.condition.EntityCondition
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.base.util.UtilMisc
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FilenameUtils;
import com.fasterxml.jackson.databind.ObjectMapper

List<Map> sharedContentListData = new ArrayList<>();

List<GenericValue> contents = EntityQuery.use(delegator).from("Content").where(EntityCondition.makeCondition("createdByUserLogin", "system")).queryList()

List<GenericValue> partyContent = delegator.findAll("PartyContent", false);

def uniquePartyContentIds = partyContent.contentId.unique { a, b -> a <=> b }

def allFileExtensionsList = []
def allFileExtensionsMap = [:]

def image = ["jpg","jpeg","png","gif","svg","tif","tiff","ps","ico","bmp","psd","eps","ai","raw"]
def audio = ["ivs","m4a","mp3","mmf","mpc","msv","voc","vox","wav","wma","cda","8svx","m2ts"]
def video = ["webm","mkv","f4v","vob","ogv","drc","ogg","gifv","avi","f4p","f4a","f4b","nsv","ts","mov","qt",
             "rmvb", "roq","mxf","mp4","3gp","3gz","m4v","m2v","mpg","m4p","mp2","mpeg","mpv","amv","asf","yuv",
             "wmv","yuv", "flv","aa","aac","aax","act","aiff","alac","amr","apc","a4","awb","dct","dvf","flac",
             "gsm", "iklax","rm"]
def zip = ["7z","rar","arj","deb","pkg","rpm","tar","tar.tz","z","zip"]
def document = ["pptx","ppt","odp","doc","docx","odt","pdf","rtf","tex","txt","wpd","mis"]
def code = ["py","class","c","cpp","cs","h","java","pl","swift","sh","vb","js","css"]

allFileExtensionsMap.put("image",image);
allFileExtensionsMap.put("audio",audio);
allFileExtensionsMap.put("video",video);
allFileExtensionsMap.put("zip",zip);
allFileExtensionsMap.put("document",document);
allFileExtensionsMap.put("code",code);

allFileExtensionsList.add(allFileExtensionsMap)

def allFileTypes = []
def fileType = [:]
def documentTypes = [:]

fileType.put("audio","fa fa-file-audio-o")
fileType.put("video","fa fa-file-video-o")
fileType.put("image","fa fa-file-picture-o")
documentTypes.put("pdf","fa fa-file-pdf-o")
documentTypes.put("excel","fa fa-file-excel-o")
documentTypes.put("ppt","fa fa-file-powerpoint-o")
documentTypes.put("pptx","fa fa-file-powerpoint-o")
documentTypes.put("odp","fa fa-file-powerpoint-o")
documentTypes.put("doc","fa fa-file-word-o")
documentTypes.put("docx","fa fa-file-word-o")
documentTypes.put("odt","fa fa-file-word-o")
documentTypes.put("txt","fa fa-file-text-o")
documentTypes.put("rtf","fa fa-file-text-o")
documentTypes.put("tex","fa fa-file-text-o")
documentTypes.put("wpd","fa fa-file-text-o")
documentTypes.put("mis","fa fa-file-text-o")
fileType.put("document",documentTypes)
fileType.put("code","fa fa-file-code-o")
fileType.put("zip","fa fa-file-zip-o")
fileType.put("others","fa fa-file-o")

allFileTypes.push(fileType)

for(def uniquePartyContentId : uniquePartyContentIds)
{
    def keyValue = null

    for (def file : contents)
    {
        if(uniquePartyContentId == file.contentId)
        {
            def sharedContentMapData = [:]
            sharedContentMapData.put("contentId",file.contentId)
            sharedContentMapData.put("contentName",file.contentName)
            sharedContentMapData.put("lastModifyDate",file.createdDate)
            for(def partyCnt : partyContent)
            {
                if (uniquePartyContentId == partyCnt.contentId)
                {
                    sharedContentMapData.put("lastModifyDate",partyCnt.fromDate)
                }
            }
            List<GenericValue> sharedFiles = EntityQuery.use(delegator).from("PartyContent").where("contentId", uniquePartyContentId).queryList();
            sharedContentMapData.put("partyId",sharedFiles.partyId)

            def fileNameExt = null ;
            for (def ext : allFileExtensionsList) {
                ext.each {
                    key, value ->
                        fileNameExt = FilenameUtils.getExtension(file.contentName);
                        if(fileNameExt != null)
                        {
                            if (value.contains(fileNameExt)) {
                                keyValue = key
                            }
                            else{
                                sharedContentMapData.put("fileTypeImage","fa fa-file-o")
                            }
                        }
                        else{
                            sharedContentMapData.put("fileTypeImage","fa fa-file-o")
                        }
                }
            }
            for (def fileTypes : allFileTypes) {
                fileTypes.each {
                    key, value ->
                        if (key == keyValue) {
                            if (value.getClass() == LinkedHashMap) {
                                value.each {
                                    keys, values ->
                                        if (keys == fileNameExt) {
                                            sharedContentMapData.put("fileTypeImage",values)
                                        }
                                }
                            } else {
                                sharedContentMapData.put("fileTypeImage",value)
                            }
                        }
                }
            }
            sharedContentListData.add(sharedContentMapData)
        }
    }
}
ObjectMapper mapper = new ObjectMapper();
mapper.writeValue(response.getWriter(), sharedContentListData);