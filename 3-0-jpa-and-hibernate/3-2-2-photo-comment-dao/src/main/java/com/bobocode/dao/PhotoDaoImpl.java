package com.bobocode.dao;

import com.bobocode.model.Photo;
import com.bobocode.model.PhotoComment;
import com.bobocode.util.ExerciseNotCompletedException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Please note that you should not use auto-commit mode for your implementation.
 */
public class PhotoDaoImpl implements PhotoDao {
    private EntityManagerFactory entityManagerFactory;

    public PhotoDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(Photo photo) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(photo);
        entityManager.getTransaction().commit();
    }

    @Override
    public Photo findById(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Photo photo = entityManager.find(Photo.class, id);
        entityManager.getTransaction().commit();
        return photo;
    }

    @Override
    public List<Photo> findAll() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<Photo> photos = entityManager.createQuery("from Photo").getResultList();

        entityManager.getTransaction().commit();
        return photos;
    }

    @Override
    public void remove(Photo photo) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Photo merge = entityManager.merge(photo);
        entityManager.remove(merge);

        entityManager.getTransaction().commit();

    }

    @Override
    public void addComment(long photoId, String comment) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Photo photo = entityManager.find(Photo.class, photoId);
        PhotoComment photoComment = new PhotoComment();
        photoComment.setText(comment);
        photoComment.setPhoto(photo);
        photoComment.setCreatedOn(LocalDateTime.now());
        photo.getComments().add(photoComment);

        entityManager.getTransaction().commit();
    }
}
