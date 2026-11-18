#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main(int argc, char *argv[]) {
    size_t megatavut = 500;
    if (argc > 1) {
        megatavut = strtoul(argv[1], NULL, 10);
    }
    size_t koko = megatavut * 1024 * 1024;

    printf("1) Varataan %zu Mt muistia...\n", megatavut);
    char *muisti = malloc(koko);
    if (muisti == NULL) {
        fprintf(stderr, "Varaus epäonnistui: muistia ei ole tarpeeksi.\n");
        return 1;
    }
    printf("Varattu. Paina Enter, niin muisti täytetään.\n");
    getchar();

    printf("2) Kirjoitetaan jokaiseen tavuun...\n");
    memset(muisti, 1, koko);
    printf("Täytetty. Katso Tehtävienhallintaa ja paina Enter vapauttaaksesi.\n");
    getchar();

    free(muisti);
    muisti = NULL;
    printf("3) Vapautettu. Paina Enter lopettaaksesi.\n");
    getchar();
    return 0;
}