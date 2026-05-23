package vn.civilpro.helper;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import vn.civilpro.model.entity.DataSource;
import vn.civilpro.repository.DataSourceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MediaStorageService {

    private final Cloudinary cloudinary;
    private final DataSourceRepository dataSourceRepository;

    @Transactional
    public String uploadMedia(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image"))
                throw new RuntimeException("Chỉ chấp nhận file ảnh");

            String folderName = "auth/images";

            DataSource dataSource = new DataSource();
            dataSource.setMediaType("IMAGE");
            dataSource.setData(file.getBytes());
            dataSource = dataSourceRepository.save(dataSource);

            String publicId = "image_" + dataSource.getId();
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", folderName,
                    "public_id", publicId,
                    "resource_type", "image"
            ));

            dataSource.setPublicId(publicId);
            dataSource.setImageUrl(result.get("secure_url").toString());
            dataSourceRepository.save(dataSource);

            return String.valueOf(dataSource.getId());
        } catch (Exception e) {
            throw new RuntimeException("Upload media failed", e);
        }
    }

    @Transactional
    public void deleteMedia(Long dataSourceId) {
        DataSource dataSource = dataSourceRepository.findById(dataSourceId)
                .orElseThrow(() -> new RuntimeException("DataSource not found"));
        try {
            if (dataSource.getPublicId() != null) {
                cloudinary.uploader().destroy(dataSource.getPublicId(), ObjectUtils.asMap(
                        "resource_type", "image"
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException("Delete media failed", e);
        }
        dataSourceRepository.delete(dataSource);
    }
}