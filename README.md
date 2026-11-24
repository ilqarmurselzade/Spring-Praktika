# Spring Praktika: Bean Scope və Dependency Injection

Texnologiya: Spring Boot 3.5, Java 17+. İşə salmaq: `TestDersApplication` sinfini Run edin (və ya `mvn spring-boot:run`).

Əsas endpoint-lər:
- `POST /api/v1/user-info/create`
- `GET /api/v1/user-info/{id}`
- `GET /api/v1/user-info/all`

Hər bölmə ayrıca commit-dir (`task-1: singleton`, `task-2: prototype` və s.). Şərh tələb edən sualların cavabları aşağıdadır, screenshot yerləri `[ ]` ilə qeyd olunub.

---
## Bölmə 1: Singleton

### Tapşırıq 6
`... yaradildi` logları (`UserInfoController`, `UserInfoService`, `UserRepository`) tətbiq açılanda **hər biri yalnız 1 dəfə** çıxır. 10 sorğu göndərəndən sonra da sayı artmır, `hashCode` da həmişə eyni qalır. Səbəb: bean-lər singleton-dur, Spring hər birini bir dəfə yaradır və bütün sorğularda eyni obyekti istifadə edir.
- [ ] Screenshot: konsol logu (10 sorğudan sonra)

### Tapşırıq 7
`POST /create` ilə yaradılan user-in id-si ilə `GET /{id}` çağıranda **eyni user** gəlir. Çünki bütün sorğular eyni repository obyektinin eyni `storage` map-ini istifadə edir. `GET /all` başlanğıcdakı 3 user və özümüzün yaratdığı user-lərin cəmini qaytarır (2 user yaratmışıqsa 5).
- [ ] Screenshot: POST /create, GET /{id}, GET /all (Postman)

### Tapşırıq 8
Singleton bean tətbiqdə **tək obyektdir** və Tomcat-in thread pool-undakı bütün thread-lər (standart olaraq 200-ə qədər) onu **eyni anda** istifadə edir.
- Adi `HashMap` thread-safe deyil: eyni anda yazanda məlumat itə, map korlana bilər. Buna görə `ConcurrentHashMap` istifadə olunub.
- Adi `long counter++` atomik deyil (oxu, artır, yaz ayrı addımlardır). İki thread eyni dəyəri oxuyub eyni id-ni verə bilər, yəni iki user eyni id ilə yaranar və biri digərini əzər. Buna görə `AtomicLong.incrementAndGet()` istifadə olunub.

---
## Bölmə 2: Prototype

### Tapşırıq 11
`UserAuditLog` (prototype) `UserInfoService`-in konstruktoruna birbaşa inject ediləndə Spring onu **yalnız bir dəfə**, service yaradılanda verir. Sonra service singleton olduğu üçün eyni `UserAuditLog` obyekti həmişə istifadə olunur. 3 user yaradanda **1 fərqli auditId** görünür (gözlənilən nəticə: 1). Prototype scope burada heç bir fayda vermir.
- [ ] Screenshot: səhv variantda log (1 auditId)

### Tapşırıq 12
Düzgün variant: `ObjectProvider<UserAuditLog>.getObject()` hər `createUser` çağırışında **yeni obyekt** verir. 3 user yaradanda 3 fərqli `auditId` çıxır.
- [ ] Screenshot: düzgün variantda log (3 fərqli auditId)

### Tapşırıq 13
`UserAuditLog`-un `@PreDestroy` metodu tətbiq bağlananda **çağırılmır**, log çıxmır. Səbəb: Spring prototype bean-i yaradıb verəndən sonra onu izləmir, saxlamır. Singleton bean-in bütün həyat dövrünü (yaradılma və məhv edilmə) container idarə edir, prototype-da isə yalnız yaradılmanı. Məhv etmə (cleanup) bean-i alan kodun məsuliyyətidir.
- [ ] Screenshot: tətbiq bağlananda konsol (UserAuditLog üçün destroy logu yoxdur)

---
## Bölmə 3: Request scope

### Tapşırıq 18
`@RequestScope` əvəzinə `@Scope("request")` (proxyMode olmadan) yazanda tətbiq **açılmır**. Xəta: `Scope 'request' is not active for the current thread; consider defining a scoped proxy for this bean if you intend to refer to it from a singleton`. Səbəb: singleton-lar (controller, service, repository) tətbiq açılanda yaradılır, o vaxt isə heç bir HTTP sorğusu yoxdur, yəni request scope aktiv deyil və Spring `RequestContext` obyekti yarada bilmir. Həll: `proxyMode = ScopedProxyMode.TARGET_CLASS` (və ya sadəcə `@RequestScope`, o artıq proxy ilə gəlir).
- [ ] Screenshot: xəta mesajı

### Tapşırıq 19
`requestContext.getClass()` çıxışı belədir: `class ...component.RequestContext$$SpringCGLIB$$0`. Yəni singleton-a real `RequestContext` yox, onun **CGLIB proxy-si** inject olunub. Proxy singleton kimi tətbiq açılanda bir dəfə inject olunur. Hər metod çağırışında (məsələn `getRequestId()`) proxy cari HTTP sorğusuna baxır, həmin sorğuya aid real `RequestContext` obyektini tapır (yoxdursa yaradır) və çağırışı ona ötürür. Beləliklə, singleton həmişə "cari sorğunun" bean-i ilə işləyir.
- [ ] Screenshot: 3 qatın requestId logları (bir sorğuda eyni, növbətidə fərqli) və sorğu müddəti

---
## Bölmə 4: Session scope

### Tapşırıq 23
Session müddəti `server.servlet.session.timeout: 1m` ilə 1 dəqiqəyə endirilib. Session bitəndən sonra (Tomcat vaxtı arxa planda yoxlayır, ona görə 1-2 dəqiqə çəkə bilər) konsolda `RecentlyViewedUsers: session bitdi, ...` logu (`@PreDestroy`) çıxır.
- [ ] Screenshot: session bitəndə `@PreDestroy` logu

### Tapşırıq 24
Chrome-da ardıcıl `/1`, `/3`, `/2`, `/1` açandan sonra `/recent` siyahısı **1, 2, 3** qaytarır (ən yenisi əvvəldə, təkrar olan `1` yenidən başa keçir). Incognito pəncərədə `/recent` **boş** gəlir. Səbəb: session scope hər brauzer sessiyasına (`JSESSIONID` cookie-si) aiddir, incognito ayrı cookie ilə yeni session açır.

Postman-da boş gələ bilər, çünki server session-u `JSESSIONID` cookie-si ilə tanıyır. Client bu cookie-ni növbəti sorğuda geri göndərməsə (cookie qəbulu söndürülübsə və ya hər sorğu ayrı yeni sessiyadadırsa), server hər dəfə yeni session yaradır və siyahı boş olur.
- [ ] Screenshot: Chrome-da `/recent` nəticəsi
- [ ] Screenshot: incognito-da `/recent` nəticəsi

### Tapşırıq 25
Siyahını request scope-da saxlasaydıq, hər sorğuda yeni obyekt yaranardı və sorğu bitəndə yox olardı. `/recent` çağıranda həmişə **boş siyahı** görərdik, çünki əvvəlki sorğularda baxılan ID-lər saxlanmazdı. İstifadəçinin bir neçə sorğu boyu davam edən məlumatı üçün session scope lazımdır.

---
## Bölmə 5: Dependency Injection

### Tapşırıq 27
`MockUserRepository` əlavə edəndən sonra tətbiq açılmır. Xəta: `NoUniqueBeanDefinitionException: expected single matching bean but found 2: inMemoryUserRepository, mockUserRepository` (IntelliJ-də `Parameter 0 of constructor in UserInfoService required a single bean, but 2 were found`). Səbəb: `UserRepository` interface-inin 2 implementasiyası var və Spring hansını seçəcəyini bilmir.
- [ ] Screenshot: xəta mesajı

### Tapşırıq 28
Birinci həll: `InMemoryUserRepository` üzərinə `@Primary` yazılıb. Kodda bu variant saxlanılıb.

### Tapşırıq 29
İkinci həll: `@Primary` silinir və service-də hansını istədiyimizi göstəririk:
```java
public UserInfoService(
        @Qualifier("mockUserRepository") UserRepository userRepository,
        ...
```
Bu halda `GET /all` 2 ədəd `Mock User` qaytarır. (Kodda `@Primary` variantı qalıb ki, tətbiq normal data ilə işləsin.)
- [ ] Screenshot: `@Qualifier` variantında `GET /all` nəticəsi

### Tapşırıq 32
`UserInfoServiceTest` Spring olmadan yazılıb: `Mockito.mock(...)` ilə mock-lar düzəldilir və `new UserInfoService(...)` ilə konstruktora verilir. Service-i field injection-a (`@Autowired private UserRepository userRepository;`) keçirəndə test **işləmir**: obyekti `new` ilə yaradanda field-ə heç kim dəyər vermir, `userRepository` `null` qalır və `NullPointerException` çıxır. Konstruktor yoxdur ki, mock-u ora verək, Spring-siz dəyəri qoymağın yolu yalnız reflection və ya `@InjectMocks`-dur. Constructor injection-da asılılıqlar konstruktorda göründüyü üçün test sadədir. Buna görə constructor injection-a qaytarılıb.

### Tapşırıq 33
Service konstruktoruna `List<UserRepository>` gələndə **2 element** gəlir: `InMemoryUserRepository` və `MockUserRepository`. Konsolda `Tapilan UserRepository sayi: 2` yazılır.
- [ ] Screenshot: konsol logu (sayı 2)

---
## Bölmə 6: Bonus

### Tapşırıq 34
`GET /api/v1/scope-demo` nəticəsi (3 dəfə çağırışdan sonra cədvəlin xanalarını doldurun):

| Sahə | Çağırış 1 | Çağırış 2 | Çağırış 3 | Dəyişir? |
|---|---|---|---|---|
| singletonHashCode | | | | Xeyr (həmişə eyni) |
| prototypeAuditId1 | | | | Bəli (hər dəfə yeni) |
| prototypeAuditId2 | | | | Bəli (id1-dən də fərqli) |
| requestId | | | | Bəli (hər sorğuda yeni) |

- [ ] Screenshot: 3 çağırışın nəticəsi

### Tapşırıq 35
`UserStatistics` `@ApplicationScope` ilə bütün tətbiq üçün **tək** obyektdir. `GET /api/v1/stats` yaradılan user sayını və `getUserById` çağırış sayını qaytarır. Fərqli brauzerlərdə sayğac ümumidir, çünki session-dan fərqli olaraq application scope bütün istifadəçilərə aiddir.
- [ ] Screenshot: iki brauzerdən `/stats`

### Tapşırıq 36
Edilməyib (məcburi deyil).

### Tapşırıq 37
`PackageLoggingBeanPostProcessor` yalnız `az.iktex` paketindəki bean-lər üçün `before initialization` və `after initialization` yazır. Hər bean üçün konsolda ardıcıllıq belədir: konstruktor, `@PostConstruct` (varsa), `[BPP] before initialization`, `[BPP] after initialization`. `@PostConstruct`-u Spring-in öz daxili post-processor-u (`CommonAnnotationBeanPostProcessor`) işlədir və o bizimkindən əvvəl işləyir, ona görə `@PostConstruct` logu bizim "before" logundan əvvəl çıxır.
- [ ] Screenshot: konsolda ardıcıllıq

---
## Şifahi müdafiə üçün qısa cavablar

- **IoC və DI fərqi:** IoC ümumi prinsipdir: obyektlərin yaradılması və idarəsi bizdən Spring container-ə keçir. DI isə IoC-nin tətbiq üsuludur: asılılıqları obyektin özü yaratmır, kənardan (konstruktor, setter) verilir.
- **Constructor injection niyə yaxşıdır:** 1) asılılıqlar `final` ola bilir (dəyişməzdir); 2) Spring-siz `new` ilə test yazmaq asandır; 3) asılılıqlar açıq görünür və çatışmayan asılılıq tətbiq açılanda dərhal xəta verir (həddindən çox parametr də kodun pis dizaynını göstərir).
- **Prototype bean-in `@PreDestroy`-u:** Spring tərəfindən heç vaxt çağırılmır, cleanup bean-i alan kodun işidir.
- **Request scope bean singleton-a inject olunanda:** singleton-a CGLIB proxy verilir. Proxy hər çağırışda cari sorğunun real bean-ini tapıb çağırışı ona ötürür.
- **Session və application scope:** session hər istifadəçiyə (brauzer sessiyasına) aiddir, məsələn səbət və ya son baxılanlar. Application scope bütün tətbiq üçün tək obyektdir, məsələn ümumi statistika sayğacı.
- **`@Primary` və `@Qualifier` birlikdə olanda:** `@Qualifier` qalib gəlir, çünki o konkret seçimi göstərir.
