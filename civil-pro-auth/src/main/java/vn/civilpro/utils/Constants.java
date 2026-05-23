package vn.civilpro.utils;

public class Constants {

    public static final String X_TOTAL_COUNT = "X-Total-Count";

    public interface HTTP_STATUS {
        public static final int SUCCESS = 200;
        public static final int CREATED = 201;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }

    public interface HTTP_MESSAGE {
        public static final String SUCCESS = "Success";
    }

    public interface ARTICLE_FEATURED {
        public static final int FEATURED = 1;
        public static final int NOT_FEATURED = 0;
    }
    public interface CATEGORY_STATUS {
        public static final int ACTIVE = 1;
        public static final int DELETED = 0;
    }
}