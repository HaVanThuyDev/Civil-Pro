//package vn.civilpro.common.base;
//
//import org.springframework.data.domain.*;
//import org.springframework.data.jpa.domain.Specification;
//import vn.civilpro.proto.PageMeta;
//import java.util.Map;
//
//public abstract class BaseGrpcService<E, ID> {
//
//    protected abstract BaseRepository<E, ID> getRepository();
//    protected abstract Specification<E> buildSpec(Map<String, String> filters);
//
//    protected Page<E> search(Map<String, String> filters,
//                             int page, int size,
//                             String sortBy, String sortDir) {
//
//        // default value an toàn
//        String sort  = (sortBy  == null || sortBy.isBlank())  ? "createdAt" : sortBy;
//        String dir   = (sortDir == null || sortDir.isBlank()) ? "DESC"      : sortDir;
//
//        Sort sorting = "ASC".equalsIgnoreCase(dir)
//                ? Sort.by(sort).ascending()
//                : Sort.by(sort).descending();
//
//        return getRepository().findAll(
//                buildSpec(filters),
//                PageRequest.of(Math.max(page, 0), size <= 0 ? 20 : size, sorting));
//    }
//
//    protected PageMeta buildMeta(Page<?> page) {
//        return PageMeta.newBuilder()
//                .setTotalElements((int) page.getTotalElements())
//                .setTotalPages(page.getTotalPages())
//                .setCurrentPage(page.getNumber())
//                .build();
//    }
//}