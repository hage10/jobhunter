package vn.trungtq.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.trungtq.jobhunter.domain.Company;
import vn.trungtq.jobhunter.domain.User;
import vn.trungtq.jobhunter.domain.response.ResultPaginationDTO;
import vn.trungtq.jobhunter.repository.CompanyRepository;
import vn.trungtq.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {

        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public void handleDeleteCompany(long id) {
        Optional<Company> company = this.companyRepository.findById(id);
        if (company.isPresent()) {
            Company companyToDelete = company.get();
            List<User> users = this.userRepository.findByCompany(companyToDelete);
            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }
    public Company handleGetCompany(long id) {
        return this.companyRepository.findById(id).orElse(null);
    }
    public ResultPaginationDTO handleGetAllCompanies(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompanies = companyRepository.findAll(spec,pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pageCompanies.getTotalElements());
        meta.setPages(pageCompanies.getTotalPages());
        rs.setMeta(meta);
        rs.setResult(pageCompanies.getContent());
        return rs;
    }
    public Company handleUpdateCompany(Company company) {
        Company curCompany = handleGetCompany(company.getId());
        if (curCompany != null) {
            curCompany.setName(company.getName());
            curCompany.setAddress(company.getAddress());
            curCompany.setDescription(company.getDescription());
            curCompany.setLogo(company.getLogo());
            return this.companyRepository.save(curCompany);
        }
        return  null;
    }
}
