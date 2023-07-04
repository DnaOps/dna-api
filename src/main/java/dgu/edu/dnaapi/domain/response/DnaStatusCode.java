package dgu.edu.dnaapi.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DnaStatusCode {

    /**
     * Basic Exception
     * */
    NOT_FOUND(404, "N000", "Page Not Found"),
    INTER_SERVER_ERROR(500,"N001","INTER SERVER ERROR"),
    INVALID_PARAMETER(400,"N002", "Invalid Parameter Value"),
    BAD_REQUEST(400, "N003", "BAD REQUEST"),

    /**
     * Client Server Error Exception
     **/

    INVALID_INPUT(400, "N100", "Invalid input value, Please check your input and constraints"),
    INVALID_POST(404, "N101", "No post has that information, Please check your request"),
    INVALID_COMMENT(404, "N102", "No comment has that information, Please check your request"),
    INVALID_SEARCH_OPTION(404, "N103", "Invalid search option, pleas check your search criteria or keywords"),
    INVALID_DELETE_COMMENT(404, "N104", "Check your Deleted CommentId"),
    INVALID_LIKE_OPERATION(404, "N105", "Check your like operation condition"),
    INVALID_File_Format(404, "N106", "Check your input file format, We don't support the file format"),



    /**
     * Auth Exception
     */

    DUPLICATE_EMAIL(400, "N200", "Duplicate Email, Please User the other email or check your email"),
    INVALID_PWD(400, "N201", "Please check your input value"),
    INVALID_AUTHOR(403, "N203", "Only Author can do request work, Please Check your authority"),
    INVALID_USER(403, "N204", "No user has that information, Please check your information"),
    INVALID_OAUTH_INFO(403, "N205", "Invalid OAuth"),
    UNVERIFIED_USER(403, "N206", "INVALID USER, Only authorized users can log in"),
    UNAUTHORIZED_REQUEST(403, "N207", "Unauthorized request, Only ADMIN Can do it"),


    /**
     * Token Exception
     * */

    TOKEN_INVALID(400, "N300", "Invalid Token, Please Check your Token"),
    REFRESH_TOKEN_INVALID(400, "N301", "Invalid Refresh Token, Please Check your Refresh Token"),

    /**
     * Server Error Exception
     **/
    AMAZONS3_IO_EXCEPTION(500, "N400", "S3 Server Problem, Check your Infra configuration"),



    /**
     * Success Code
     * */

    OK(200, "Y000", "Okay"),
    ;

    private int status;
    private String code;
    private String errorMessage;
}
