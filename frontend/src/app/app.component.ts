import {Component, Inject, OnDestroy, OnInit, Renderer2} from '@angular/core'
import {OperationService} from './operation/operation.service'
import {Subject} from 'rxjs'
import {DOCUMENT} from '@angular/common'
import {InfoService} from './info/info.service'
import {takeUntil} from 'rxjs/operators'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {

  private readonly darkThemeClass = 'dark-theme'
  private readonly unsubscribe$ = new Subject<void>()
  readonly activeOperation$ = this.operationService.isActiveOperation()

  constructor(
    @Inject(DOCUMENT) private readonly document: Document,
    private readonly renderer: Renderer2,
    private readonly infoService: InfoService,
    private readonly operationService: OperationService
  ) {
  }

  ngOnInit() {
    this.infoService
      .isDarkTheme()
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe((isDarkTheme) => {
        if (isDarkTheme) {
          this.renderer.addClass(this.document.body, this.darkThemeClass)
        } else {
          this.renderer.removeClass(this.document.body, this.darkThemeClass)
        }
      })
  }

  ngOnDestroy() {
    this.unsubscribe$.next()
    this.unsubscribe$.complete()
  }
}
